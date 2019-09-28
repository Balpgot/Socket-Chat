package ru.tsindrenko;

import java.time.LocalDateTime;

public class Mute {
    private User user;
    private LocalDateTime mute_expiration;

    Mute(User user, LocalDateTime mute_expiration){
        this.user = user;
        this.mute_expiration = mute_expiration;
    }

    public void muteForSomeMinutes(int minutes){
        this.mute_expiration = mute_expiration.plusMinutes(minutes);
    }

    public void muteForSomeHours(int hours){
        this.mute_expiration = mute_expiration.plusHours(hours);
    }

    public void muteForSomeDays(int days){
        this.mute_expiration = mute_expiration.plusDays(days);
    }

    public void muteForWeek(){
        this.mute_expiration = mute_expiration.plusWeeks(1);
    }

    public void muteForMonth(){
        this.mute_expiration = mute_expiration.plusMonths(1);
    }

    //геттеры и сеттеры
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getMute_expiration() {
        return mute_expiration;
    }

    public void setMute_expiration(LocalDateTime mute_expiration) {
        this.mute_expiration = mute_expiration;
    }
}
