import com.google.gson.Gson;
	import com.google.gson.JsonObject;
	import java.io.FileReader;
	import java.math.BigInteger;
	import java.util.ArrayList;
	import java.util.List;

	public class SecretSharing {

	    public static void main(String[] args) throws Exception {
	        Gson gson = new Gson();
	        JsonObject jsonObject = gson.fromJson(new FileReader("testcase.json"), JsonObject.class);
	        for (var testcase : jsonObject.getAsJsonArray("testcases")) {
	            JsonObject testObj = testcase.getAsJsonObject();
	            int n = testObj.getAsJsonObject("keys").get("n").getAsInt();
	            int k = testObj.getAsJsonObject("keys").get("k").getAsInt();

	            List<BigInteger> xValues = new ArrayList<>();
	            List<BigInteger> yValues = new ArrayList<>();

	            for (String key : testObj.keySet()) {
	                if (key.equals("keys")) continue;

	                BigInteger x = new BigInteger(key);
	                JsonObject point = testObj.getAsJsonObject(key);
	                int base = point.get("base").getAsInt();
	                BigInteger y = new BigInteger(point.get("value").getAsString(), base);

	                xValues.add(x);
	                yValues.add(y);

	                if (xValues.size() == k) break; // We only need k points
	            }

	            // Calculate the constant term (secret) using Lagrange interpolation at x=0
	            BigInteger secret = lagrangeInterpolationAtZero(xValues, yValues);
	            System.out.println("The constant term (secret) is: " + secret);
	        }
	    }

	    public static BigInteger lagrangeInterpolationAtZero(List<BigInteger> xValues, List<BigInteger> yValues) {
	        BigInteger result = BigInteger.ZERO;

	        for (int i = 0; i < xValues.size(); i++) {
	            BigInteger term = yValues.get(i);

	            for (int j = 0; j < xValues.size(); j++) {
	                if (i != j) {
	                    BigInteger numerator = xValues.get(j).negate();
	                    BigInteger denominator = xValues.get(i).subtract(xValues.get(j));
	                    term = term.multiply(numerator).divide(denominator);
	                }
	            }

	            result = result.add(term);
	        }

	        return result;
	    }
	}
