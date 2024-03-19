package com.example.firebase.TemporaryPassword;

public class Tempo {
    int Tempo;
    private String UserTempo,dateTempo,timeTempo;
    private int OpenPassTempo,ClosePassTemPo;

    public Tempo(int tempo, String userTempo, String dateTempo, String timeTempo, int openPassTempo, int closePassTemPo) {
        Tempo = tempo;
        UserTempo = userTempo;
        this.dateTempo = dateTempo;
        this.timeTempo = timeTempo;
        OpenPassTempo = openPassTempo;
        ClosePassTemPo = closePassTemPo;
    }

    public Tempo(String userTempo, int openPassTempo, int closePassTemPo,String dateTempo, String timeTempo) {
        UserTempo = userTempo;
        this.dateTempo = dateTempo;
        this.timeTempo = timeTempo;
        OpenPassTempo = openPassTempo;
        ClosePassTemPo = closePassTemPo;
    }

    public Tempo() {

    }

    public int getTempo() {
        return Tempo;
    }

    public void setTempo(int tempo) {
        Tempo = tempo;
    }

    public String getUserTempo() {
        return UserTempo;
    }

    public void setUserTempo(String userTempo) {
        UserTempo = userTempo;
    }

    public String getDateTempo() {
        return dateTempo;
    }

    public void setDateTempo(String dateTempo) {
        this.dateTempo = dateTempo;
    }

    public String getTimeTempo() {
        return timeTempo;
    }

    public void setTimeTempo(String timeTempo) {
        this.timeTempo = timeTempo;
    }

    public int getOpenPassTempo() {
        return OpenPassTempo;
    }

    public void setOpenPassTempo(int openPassTempo) {
        OpenPassTempo = openPassTempo;
    }

    public int getClosePassTemPo() {
        return ClosePassTemPo;
    }

    public void setClosePassTemPo(int closePassTemPo) {
        ClosePassTemPo = closePassTemPo;
    }
}
