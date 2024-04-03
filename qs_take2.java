import java.math.BigInteger;
import java.util.*;
import java.io.*;

public class qs_take2{
    public static BigInteger zero = new BigInteger("0");
	public static BigInteger one = new BigInteger("1");
	public static BigInteger two = new BigInteger("2");

    public static BigInteger powersOfTwo(BigInteger n) {
		
		BigInteger x = new BigInteger("0");
		
		// while even
		while(n.mod(two).equals(zero)) {
			
			n = n.divide(two);
			x = x.add(one);
			
		}
		
		return x;
		
	}
	
	public static BigInteger shanksTonelli(BigInteger n, BigInteger p) {
		
		// p - 1 = Q * 2^S
		BigInteger S = powersOfTwo(p.subtract(one));
		//System.out.println("S: " + S);
		
		// Q = (p - 1) / 2^S
		BigInteger Q = (p.subtract(one)).divide(two.pow(S.intValue()));
		//System.out.println("Q: " + Q);

		// now we need to find a quadratic non-residue
		// we will use euler's criterion
		BigInteger z = zero;
		
		BigInteger loop = one;
		
		for(loop = one; loop.compareTo(p) == -1 ; loop = loop.add(one)) {
			
			// if (i ^ ( p-1 / 2 ) ) mod p == p - 1
			// that means it equals -1 mod p
			// which makes i an quadratic non-residue
			if(loop.modPow((p.subtract(one)).divide(two), p).equals(p.subtract(one))) {
				z = loop;
				break;
			}
		}
        //System.out.println(z);
		
		// let M = S, c = z^Q, t = n^Q, R = n ^ ( Q+1 / 2) all mod p
		BigInteger M = S.mod(p);
		BigInteger c = z.modPow(Q, p);
		BigInteger t = n.modPow(Q, p);
		BigInteger R = n.modPow((Q.add(one)).divide(two), p);
		BigInteger retru = BigInteger.ZERO;
        

		if(t.equals(zero)){
			return zero;
		}
		if(t.equals(one)){
			return R;
		}
		// if t = 0, return r = 0
		// if t = 1, return r = R
		boolean flag= true;
		while(flag == true) {
			
			//System.out.println(t);
			int i = 1;
			
			// while t^2^i != 1, increment i
			while(!(t.pow(2).pow(i).mod(p).equals(one))) {
			// repeated squaring to find the least i , 0 < i < M, such that t ^ 2 ^i = 1 mod p
				i++;
                if(i == M.intValue()){
                    break;
                }
			}

			if(i==M.intValue()){
                break;
            }

			BigInteger newI = new BigInteger(Integer.toString(i));
			
			// b = c ^ 2 ^ M-i-1 mod p
			// same as c*c ^ M-i-1 mod p
			// this line gave me problems when i was 
			//BigInteger b = c.pow((int) Math.pow(2, M.intValue() - i - 1)).mod(p);
			BigInteger abc = M.subtract(newI).subtract(BigInteger.ONE);
            //System.out.println(abc);
			BigInteger def = BigInteger.TWO.pow((int) abc.intValue());
			BigInteger b = c.pow((int) def.intValue()).mod(p);
			
			M = newI.mod(p);
			c = (b.multiply(b)).mod(p);
			t = (t.multiply(b).multiply(b)).mod(p);
			R = (R.multiply(b)).mod(p);
			if(t.equals(one)){
				flag=false;
				retru = R;
			}
			if(t.equals(zero)){
				flag=false;
				retru = BigInteger.ONE;
			}
			
		}
		return retru;
	}

    public static void main(String[] args) throws IOException{
		BufferedWriter file = new BufferedWriter(new FileWriter(args[0]));
        BigInteger n = new BigInteger("9209839122440374002906008377605580208264841025166426304451583112053");
        //BigInteger n = new BigInteger("920983912245166426304451583112053");
		//BigInteger n = new BigInteger("227179");
        //BigInteger bound = new BigInteger("30");
        double bound = 300000;
        boolean[] primes = new boolean[(int)bound + 1];
		
		for(int i = 0; i < primes.length; i++)
			primes[i] = true;
		
		// from i = 2 to sqrt(bound)
		for(int i = 2; i < (int)Math.sqrt(bound) + 1; i++) {
			if(primes[i] == true) {
				int j = i*i;
				while(j <= bound) {
					primes[j] = false;
					j += i;
				}
			}
		}


        ArrayList<Integer> factorBase = new ArrayList<Integer>();
        factorBase.add(-1);
		
		for(int i = 2; i < primes.length; i++) {
			if(primes[i]) {
				BigInteger bigIntI = new BigInteger(Integer.toString(i));
                //System.out.println(bigIntI);
				
				if(n.modPow((bigIntI.subtract(one)).divide(two), bigIntI).equals(one)){
					factorBase.add(bigIntI.intValue());
                    //System.out.println(bigIntI);
                }
					
			}
			
		}
		System.out.println("Factor base size: " + factorBase.size());

        BigInteger x = n.sqrt();
        //BigInteger x = n.sqrt();
        //BigInteger[] relations = new BigInteger[factorBase.size()*20];
		int[] relations = new int[100000000];
		int[] vectors = new int[100000000];

        for(int i = 0; i < relations.length ; i++) {
			// compute number to be factorized from sequence
			BigInteger bigIntI = new BigInteger(Integer.toString(i));
			// (x + i)^2 - i - N
			// we can also do (x + i)^2 - N if the - i doesn't work efficiently
			int V = ((bigIntI.subtract(x)).pow(2)).subtract(n).intValue();

			relations[i] = V;
			vectors[i] = V;
			//System.out.print(V+" ");
			
		}
        //System.out.println();
		
		for (int i=0; i<factorBase.size(); i++){
			int p = factorBase.get(i);
			System.out.println("BASE: "+ p);
            if(p==-1){
                for(int j=0; j<relations.length;j++){
                    if(relations[j]<0){
						relations[j]/=p;
					}
                }
                continue;
                
            }
			BigInteger root = one;
			if(p % 4 == 3){
				root = n.modPow(BigInteger.valueOf(p).add(one).divide(new BigInteger("4")),BigInteger.valueOf(p));
			}else{
				root = shanksTonelli(n, BigInteger.valueOf(p));
			}
            //System.out.println(root);
            if(root.equals(zero)){
                for(int j=0; j<relations.length;j++){
                    if(relations[j]%p==0){
                        relations[j] /=p;
                    }
                }
                continue;
            }
			int First_root = root.add(x).mod(BigInteger.valueOf(p)).intValue();
			//System.out.println("First Root :" + First_root);
            int second_root = BigInteger.valueOf(p).subtract(root).add(x).mod(BigInteger.valueOf(p)).intValue();
			//System.out.println("Second Root: " + second_root);
			while(First_root < relations.length || second_root < relations.length){
				if(First_root < relations.length){
					if(relations[First_root] % p == 0){
						relations[First_root]/=p;
					}
				}
				if(second_root < relations.length){
					if(relations[second_root] % p == 0){
						relations[second_root]/=p;
					}
				}
				First_root += p;
				second_root += p;
			}
		}
        
		
		int abcd = 0;
		for(int i = 0; i < relations.length ; i++) {
			if(relations[i] == 1){
				int a = vectors[i];
				for(int j = 0; j < factorBase.size(); j++){
					if(a % factorBase.get(j) == 0){
						file.write(1 + " ");
					}
					else{
						file.write(0 + " ");
					}
				}
				file.write('\n');
			}
			
		}
		file.close();
		System.out.println("Smooth Relations: " + abcd);

        
        /* 
        
            for(int k = 0; k < relations.length ; k++) {
                System.out.print(relations[k]+" "); 
            }
            System.out.println();
		}
		*/
    }
}