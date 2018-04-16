package vendingmachine;

public interface OnActionCallback {

    void onCoinInserted(double value);

    void onTransactionCancel();

}
