import java.math.BigInteger;
import java.util.Random;

public class FermatTesting {
  public static boolean checkPrime(BigInteger n, int iterations) {
    Random rng = new Random();

    if (n.equals(BigInteger.ONE)) return false;

    for (int i=0; i<iterations; i++) {
      // Create an integer within the interval [1,n-1]
      BigInteger a = new BigInteger(n.bitLength(),rng);
      while (BigInteger.ONE.compareTo(a) > 0 || a.compareTo(n) >= 0) {
        a = new BigInteger(n.bitLength(),rng);
      }

      // a^(n-1)
      // Repeated until a!=1, thus proving it cannot be prime
      a = a.modPow(n.subtract(BigInteger.ONE),n);

      if (!a.equals(BigInteger.ONE)) return false;
    }

    return true;
  }

  public static void main(String[] args) {
    System.out.printf("checkprime(10) is %b%n", checkPrime(BigInteger.valueOf(10L), 20));
  }
}
