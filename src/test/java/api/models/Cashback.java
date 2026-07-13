package api.models;

public class Cashback {
    private Double pending;
    private Double Confirmed;
    private Double declined;


    public Double getPending() {
        return pending;
    }

    public Double getConfirmed() {
        return Confirmed;
    }

    public Double getDeclined() {
        return declined;
    }
 public void setPending(Double pending) {
        this.pending = pending;
    }

    public void setConfirmed(Double confirmed) {
        Confirmed = confirmed;
    }

    public void setDeclined(Double declined) {
        this.declined = declined;
    }


    @Override
    public String toString() {
        return "Cashback{" +
                "pending=" + pending +
                ", Confirmed=" + Confirmed +
                ", declined=" + declined +
                '}';
    }

}
