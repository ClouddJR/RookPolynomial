package com.clouddroid.rookpolynomial.models;

import android.content.Context;


public class MatrixElement extends android.support.v7.widget.AppCompatButton {

  private boolean isClicked;

  public MatrixElement(Context context) {
    super(context);
  }

  public boolean isClicked() {
    return isClicked;
  }

  public void setClicked(boolean clicked) {
    isClicked = clicked;
  }
}
