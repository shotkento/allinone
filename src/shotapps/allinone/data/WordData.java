package shotapps.allinone.data;

public class WordData {
    private int id;
    private String engWord;
    private String jpnWord;
    private int sentNum1;
    private int sentNum2;
    private int correct;
    private int count;
    private boolean checked;

    public int getId() {
        return id;
    }

    public String getEngWord() {
        return engWord;
    }

    public String getJpnWord() {
        return jpnWord;
    }

    public int getSentNum1() {
        return sentNum1;
    }

    public int getSentNum2() {
        return sentNum2;
    }

    public int getCorrect() {
        return correct;
    }

    public int getCount() {
        return count;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEngWord(String engWord) {
        this.engWord = engWord;
    }

    public void setJpnWord(String jpnWord) {
        this.jpnWord = jpnWord;
    }

    public void setSentNum1(int sentNum1) {
        this.sentNum1 = sentNum1;
    }

    public void setSentNum2(int sentNum2) {
        this.sentNum2 = sentNum2;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setChecked(int checked) {
        if (checked == 0) {
            this.checked = false;
        } else {
            this.checked = true;
        }
    }
}
