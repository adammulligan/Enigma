import java.util.Random;
import java.math.BigInteger;

public class LehmannTest implements PrimeTest {
  public boolean checkPrime(BigInteger n, int iterations) {
    if (n.equals(BigInteger.ONE)) return false;

    for (int i=0;i<iterations;i++) {
      BigInteger a;
      do {
        // Pick a random integer a
        a = new BigInteger(n.bitLength(),new Random());
      } while (BigInteger.ONE.compareTo(a) >= 0 || a.compareTo(n) < 0); // 1 <= a < n

      a = a.modPow(n.subtract(BigInteger.ONE).divide(BigInteger.valueOf(2L)),n);

      // If x=1 or x=-1, then n is probably prime
      if (a.equals(BigInteger.ONE) || a.equals(BigInteger.valueOf(-1L).mod(n))) return false;
    }

    return true;
  }
}
