package api.utils;
import api.models.Cashback;

public class EarningsDataStore {
    public static volatile Cashback apiCashback;
    public static volatile Double uiPendingPayments;
    public static volatile Double uiConfirmedEarnings;
    public static volatile Double uiDeclinedAmount;
}