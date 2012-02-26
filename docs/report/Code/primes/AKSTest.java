import java.math.BigInteger;

public class AKSTest implements PrimeTest {
  public boolean checkPrime(BigInteger n, int iterations) {
    

    return false;
  }

  /**
   * Generates an appropriate value of r
   *
   */
  public BigInteger generateR(BigInteger n) {
  }

  /**
   * Uses Fermat's Little Theorem to determine is a number is prime.
   *
   */
  public boolean isPrime(BigInteger n) {
    BigInteger TWO = BigInteger.valueOf(2L);

    while ((TWO.multiply(TWO).compareTo(n) <= 0) {
      if (n.mod(TWO).compareTo(BigInteger.ZERO)==0) return false;
      TWO = TWO.add(BigInteger.ONE);
    }

    return true;
  }

  /**
   * Checks if a number is a power of 2
   *
   */
  public boolean isPower(BigInteger n) {
    return false;
  }
}
