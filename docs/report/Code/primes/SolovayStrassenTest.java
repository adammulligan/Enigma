import java.math.BigInteger;
import java.util.Random;

public class SolovayStrassenTest implements PrimeTest {
  public boolean checkPrime(BigInteger n, int iterations) {
    BigInteger n_one = n.subtract(BigInteger.ONE);

    for (int i=0;i<iterations;i++) {
      BigInteger a = new BigInteger(n.subtract(BigInteger.valueOf(2L)).bitLength(),new Random()).add(BigInteger.valueOf(2L));
      int x=jacobiSymbol(a,n);

      // r = a^(n-1)/2 mod n
      BigInteger r = a.modPow(n_one.divide(BigInteger.valueOf(2L)),n);
      // if (a|n)=0 or r!=x and r!=(n-1)
      if (x==0 || (r.compareTo(BigInteger.valueOf(x))!=0) && (r.compareTo(n_one)!=0)) {
        return false;
      }
    }

    return true;
  }

  // Based on Algorith 2.149 in Alfred-Menezes:1996kx
  public static int jacobiSymbol(BigInteger a, BigInteger n){
      int j = 1;

      BigInteger ZERO  = BigInteger.ZERO;
      BigInteger ONE   = BigInteger.ONE;
      BigInteger TWO   = BigInteger.valueOf(2L);
      BigInteger THREE = BigInteger.valueOf(3L); 
      BigInteger FOUR  = BigInteger.valueOf(4L);
      BigInteger FIVE  = BigInteger.valueOf(5L);
      BigInteger EIGHT = BigInteger.valueOf(8L);

      BigInteger res;

      while (a.compareTo(ZERO) != 0){
        // While a % 2 == 0
        while (a.mod(TWO).compareTo(ZERO) == 0){
          a = a.divide(TWO); // a / 2
          res = n.mod(EIGHT); // n % 8

          if (res.compareTo(THREE) == 0 || res.compareTo(FIVE) == 0) j = -1*j;
        }
            
        BigInteger temp = a;
        a = n;
        n = temp;
        
        if (a.mod(FOUR).compareTo(THREE) == 0 && n.mod(FOUR).compareTo(THREE) == 0) j = -1 * j;
        a = a.mod(n);
      }
        
      if (n.compareTo(ONE) != 0) j = 0;

      return j;
    }
}
