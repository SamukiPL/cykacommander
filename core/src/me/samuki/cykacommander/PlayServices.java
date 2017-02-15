package me.samuki.cykacommander;

interface PlayServices
{
    void signIn();
    void signOut();
    void rateGame();
    void unlockAchievement(int score, int gamesPlayed);
    void submitScore(int highScore);
    void showAchievement();
    void showScore();
    boolean isSignedIn();

}
