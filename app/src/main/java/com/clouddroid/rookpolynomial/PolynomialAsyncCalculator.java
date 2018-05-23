package com.clouddroid.rookpolynomial;

import android.os.AsyncTask;
import android.text.Spanned;
import com.clouddroid.rookpolynomial.models.RookPolynomial;


public class PolynomialAsyncCalculator extends AsyncTask<int[][], Void, Spanned> {

  interface CalculationListener {

    void onCalculationFinished(Spanned result);
  }

  private CalculationListener listener;

  PolynomialAsyncCalculator(CalculationListener listener) {
    this.listener = listener;
  }

  @Override
  protected void onPostExecute(Spanned spanned) {
    listener.onCalculationFinished(spanned);
    super.onPostExecute(spanned);
  }

  @Override
  protected Spanned doInBackground(int[][]... ints) {
    RookPolynomial polynomial = new RookPolynomial((ints[0]));
    return polynomial.toHtmlString();
  }
}
