package com.clouddroid.rookpolynomial;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.clouddroid.rookpolynomial.models.MatrixElement;

public class MainActivity extends AppCompatActivity {

    FrameLayout matrixHolder;
    EditText numberOfColumnsAndRowsEditText;
    MatrixElement[][] elementsInMatrix;
    ProgressDialog dialog;
    int numberOfColumnsAndRows = 0;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        initButtons();
        initEditText();
        initMatrixScrollView();
    }

    private void initButtons() {
        Button generateMatrixButton = findViewById( R.id.generateButton );
        generateMatrixButton.setOnClickListener( v -> {
            if( isEnteredNumberCorrect() ) {
                readNumberOfColumnsAndRows();
                initElementsArray();
                drawMatrix();
                showCalculateButton();
                hideResultTextView();
                hideKeyboard();
            } else {
                hideMatrixView();
                hideCalculateButton();
                hideResultTextView();
            }
        } );
    }

    private void initEditText() {
        numberOfColumnsAndRowsEditText = findViewById( R.id.numberEditText );
    }

    private void initMatrixScrollView() {
        matrixHolder = findViewById( R.id.matrixView );
    }

    private boolean isEnteredNumberCorrect() {
        if( !numberOfColumnsAndRowsEditText.getText().toString().equals( "" ) ) {
            int enteredNumber = Integer.parseInt( numberOfColumnsAndRowsEditText.getText().toString() );
            if( isCorrectNumber( enteredNumber ) ) {
                return true;
            } else {
                displayErrorToastWithMessage( getString( R.string.enter_number_from_range ) );
            }
        } else {
            displayErrorToastWithMessage( getString( R.string.enter_number_from_range ) );
        }
        return false;
    }

    public void readNumberOfColumnsAndRows() {
        numberOfColumnsAndRows = Integer.parseInt( numberOfColumnsAndRowsEditText.getText().toString() );
    }


    private boolean isCorrectNumber( int number ) {
        return number > 1 && number <= 10;
    }

    private void displayErrorToastWithMessage( String errorMessage ) {
        Toast.makeText( this, errorMessage, Toast.LENGTH_LONG ).show();
        hideCalculateButton();
    }

    private void hideCalculateButton() {
        findViewById( R.id.calculateButton ).setVisibility( View.GONE );
    }

    private void initElementsArray() {
        elementsInMatrix = new MatrixElement[numberOfColumnsAndRows][numberOfColumnsAndRows];
    }

    private void drawMatrix() {
        hideMatrixView();
        TableLayout tableLayout = new TableLayout( this );

        for( int i = 0; i < numberOfColumnsAndRows; i++ ) {
            TableRow tableRow = new TableRow( this );

            for( int j = 0; j < numberOfColumnsAndRows; j++ ) {
                MatrixElement element = generateElementForRow();
                addElementToList( element, i, j );
                tableRow.addView( element );
            }
            tableLayout.addView( tableRow );
        }
        matrixHolder.addView( tableLayout );
    }

    private void hideMatrixView() {
        matrixHolder.removeAllViewsInLayout();
    }

    private MatrixElement generateElementForRow() {
        MatrixElement element = new MatrixElement( this );
        element.setOnClickListener( onMatrixItemClicked );
        element.setTextSize( TypedValue.COMPLEX_UNIT_SP, 17 );

        float dpWidth = getScreenWidthInDp();

        TableRow.LayoutParams rowParams;
        if( numberOfColumnsAndRows > 7 ) {
            rowParams = new TableRow.LayoutParams(
                    inDensityPixels( (int) dpWidth / (int) ( numberOfColumnsAndRows * 1.3 ) ),
                    inDensityPixels( (int) dpWidth / (int) ( numberOfColumnsAndRows * 1.3 ) ) );
        } else {
            rowParams = new TableRow.LayoutParams(
                    inDensityPixels( 35 ),
                    inDensityPixels( 35 ) );
        }
        rowParams.setMargins( 3, 3, 3, 3 );
        element.setLayoutParams( rowParams );
        element.setBackground( getResources().getDrawable( R.drawable.matrix_element ) );
        return element;
    }

    private int getScreenWidthInDp() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics( outMetrics );

        float density = getResources().getDisplayMetrics().density;
        return (int) ( outMetrics.widthPixels / density );
    }

    private int inDensityPixels( int pixels ) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) ( pixels * scale + 0.5f );
    }

    private void addElementToList( MatrixElement element, int row, int column ) {
        elementsInMatrix[row][column] = element;
    }

    private void showCalculateButton() {
        Button calculateButton = findViewById( R.id.calculateButton );
        calculateButton.setVisibility( View.VISIBLE );
        calculateButton.setOnClickListener( v -> {
            int[][] polynomialMatrix = generateMatrixFromView();
            calculateAndDisplayPolynomial( polynomialMatrix );
        } );
    }

    private int[][] generateMatrixFromView() {
        int[][] matrix = new int[numberOfColumnsAndRows][numberOfColumnsAndRows];

        for( int i = 0; i < numberOfColumnsAndRows; i++ ) {
            for( int j = 0; j < numberOfColumnsAndRows; j++ ) {
                matrix[i][j] = elementsInMatrix[i][j].isClicked() ? 1 : 0;
            }
        }
        return matrix;
    }

    private void calculateAndDisplayPolynomial( int[][] polynomialMatrix ) {
        showProgressDialog();
        executeCalculationInBackground( polynomialMatrix );
    }

    private void showProgressDialog() {
        dialog = new ProgressDialog( this );
        dialog.setMessage( getString( R.string.calculating_polynomial ) );
        dialog.setTitle( R.string.please_wait );
        dialog.setCancelable( true );
        dialog.show();
    }

    private void executeCalculationInBackground( int[][] polynomialMatrix ) {
        PolynomialAsyncCalculator calculator = new PolynomialAsyncCalculator( calculationListener );
        calculator.execute( polynomialMatrix );
    }


    private void hideResultTextView() {
        findViewById( R.id.resultTextView ).setVisibility( View.GONE );
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if( view != null ) {
            InputMethodManager imm = (InputMethodManager) getSystemService( Context.INPUT_METHOD_SERVICE );
            if( imm != null ) {
                imm.hideSoftInputFromWindow( view.getWindowToken(), 0 );
            }
        }
    }

    View.OnClickListener onMatrixItemClicked = view -> {
        MatrixElement chosenElement = (MatrixElement) view;
        if( chosenElement.isClicked() ) {
            chosenElement.setBackground( getResources().getDrawable( R.drawable.matrix_element ) );
        } else {
            chosenElement.setBackgroundColor( getResources().getColor( android.R.color.black ) );
        }

        chosenElement.setClicked( !chosenElement.isClicked() );
    };

    PolynomialAsyncCalculator.CalculationListener calculationListener = result -> {
        setTextResultAndDismissDialog( result );
        scrollToBottom();
    };

    private void setTextResultAndDismissDialog( Spanned result ) {
        TextView resultTextView = findViewById( R.id.resultTextView );
        resultTextView.setVisibility( View.VISIBLE );
        resultTextView.setText( result );
        dialog.dismiss();
    }

    private void scrollToBottom() {
        ScrollView mainScrollView = findViewById( R.id.mainScrollView );
        mainScrollView.post( () -> mainScrollView.fullScroll( ScrollView.FOCUS_DOWN ) );
    }

}
