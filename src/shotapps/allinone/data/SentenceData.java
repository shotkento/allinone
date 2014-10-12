package shotapps.allinone.data;

public class SentenceData {
    private int id;
    private String engSent;
    private String jpnSentOrder;
    private String jpnSentNormal;
    private int correct;
    private int count;
    private int checked;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEngSent() {
        return engSent;
    }

    public void setEngSent(String engSent) {
        this.engSent = engSent;
    }

    public String getJpnSentOrder() {
        return jpnSentOrder;
    }

    public void setJpnSentOrder(String jpnSentOrder) {
        this.jpnSentOrder = jpnSentOrder;
    }

    public String getJpnSentNormal() {
        return jpnSentNormal;
    }

    public void setJpnSentNormal(String jpnSentNormal) {
        this.jpnSentNormal = jpnSentNormal;
    }

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }

}
