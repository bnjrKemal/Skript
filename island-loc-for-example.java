// Java
public class Main {
    
	public static void main(String[] args) {
        	getFreeLocation(1, 90); //Kaç tane lokasyon verir formülü; (2f+1)^2
	}
	
	private static void getFreeLocation(int f, int k){
		for(int x = f*-1; x<=f; x++){
			for(int z = f*-1; z<=f; z++){
                		//is air
                		/*return */System.out.println("(" + x * k + ", " + z * k + ")");
            		}
        	}
        	getFreeLocation(f++, k);
	}
	
	private static void findMainLocation(Location location, int k){
        	int hX = location.getX() + (k/2);
        	int hZ = location.getZ() + (k/2);
        	int x = hX - Math.mod(hX,  k);
        	int z = hZ - Math.mod(hZ,  k);
        	System.out.println(x + "," + z);
	}
	
}

/* -> Output
(-2, -2)
(-2, -1)
(-2, 0)
(-2, 1)
(-2, 2)
(-1, -2)
(-1, -1)
(-1, 0)
(-1, 1)
(-1, 2)
(0, -2)
(0, -1)
(0, 0)
(0, 1)
(0, 2)
(1, -2)
(1, -1)
(1, 0)
(1, 1)
(1, 2)
(2, -2)
(2, -1)
(2, 0)
(2, 1)
(2, 2)
*/
