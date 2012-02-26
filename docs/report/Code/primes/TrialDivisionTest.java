public class TrialDivisionTest {
  public int checkPrime(int n) {
    // If even..
    if (n%2 == 0) return 2;

    for (int x=3;x<(int)Math.sqrt((double)n);x+=2) {
      if (n%x == 0) return x;
    }

    return -1;
  }
}
