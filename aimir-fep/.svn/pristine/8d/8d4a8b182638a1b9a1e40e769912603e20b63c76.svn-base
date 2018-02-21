import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class DecodeTest {

	public static void main(String[] args){
		
		String fwInput = "272";
		String fwVer =  Hex.decode(DataUtil.get2ByteToInt(Integer.parseInt(fwInput)));
		System.out.println(fwVer);
		String nfwVer = Double.parseDouble(fwVer.substring(0, 2) + "." + fwVer.substring(2, 4)) + "";
		System.out.println(nfwVer);
	}
}
