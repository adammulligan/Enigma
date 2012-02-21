import java.math.BigInteger;
import java.util.Random;

public class MillerRabinTest implements PrimeTest {
  public boolean checkPrime(BigInteger n, int iterations) {
    if (n.equals(BigInteger.valueOf(2L))) return true; // 2 is prime

    if (n.equals(BigInteger.ZERO) || // n==0
        n.equals(BigInteger.ONE)  || // n==1
        n.mod(BigInteger.valueOf(2L)).equals(BigInteger.ZERO)) { // n is even
      return false;
    }

    // 2 ^ s * r
    int s=0;
    BigInteger r,n_one;
    r = n_one = n.subtract(BigInteger.ONE);

    // Halve r until r is odd
    while (r.mod(BigInteger.valueOf(2L)).equals(BigInteger.ZERO)) {
      r = r.divide(BigInteger.valueOf(2L));
      s++;
    }

    for (int i=0;i<iterations;i++) {
      // Create a random number between 1 and n-1
      BigInteger a;
      do {
        int min = BigInteger.ONE.bitLength();
        int max = n_one.bitLength();
        a = new BigInteger(new Random().nextInt(max - min + 1)+min,new Random());
      } while (BigInteger.ONE.compareTo(a) > 0 || a.compareTo(n) >= 0);

      // y = a^r mod n
      BigInteger y = a.modPow(r,n);

      // While y != 1 and y != n-1
      if (!y.equals(BigInteger.ONE) && !y.equals(n_one)) {
        for (int j=0;j<s;j++) {
          // y = y^2 mod n
          y = y.modPow(BigInteger.valueOf(2L),n);

          // if y == 1, n is prime
          if (y.equals(BigInteger.ONE)) return false;

          // if y == n-1, n is composite
          if (y.equals(n_one)) return true;
        }
      }
    }

    return false;
  }
}
