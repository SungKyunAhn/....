import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.penta.nuritelecom.nuriclient;
import com.penta.nuritelecom.nuritelecom;


class pkiTest 
{
  final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
  public static String PrintHex(byte[] bytes, int bytes_len)
  {
    char[] hexChars = new char[bytes.length * 2];
    for ( int j = 0; j < bytes_len; j++ ) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = hexArray[v >>> 4];
      hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return "HEX(" + bytes_len + ")" + new String(hexChars);
  }

  public static byte[] hexToByteArray(String hex) {
    if (hex == null || hex.length() == 0) {
        return null;
    }
 
    byte[] ba = new byte[hex.length() / 2];
    for (int i = 0; i < ba.length; i++) {
        ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
    }
    return ba;
  }

  public static byte[] file2byte(String filepath) {
    byte[] array = "".getBytes();
    try {
      array = Files.readAllBytes(new File(filepath).toPath());
    }catch (IOException e) { }

    return array;
  }
  public static void main(String args[])
  {

    nuritelecom iNuriServer = new nuritelecom();
    nuriclient iNuriClient = new nuriclient();
    int ret = 0;

  System.out.println("JAVA - -------------IS_IoT_PKI_Encrypt Start at Server-----------------");
   byte[] plain_pki = hexToByteArray("FFE0FFE0FFE0FFE0FFE0FFE0FFE0FFE0");
    Integer plain_pki_len = plain_pki.length;
    byte[] cipher_pki = new byte[1000];
    Integer cipher_pki_len = cipher_pki.length;
    byte[] prikey_buffer_pki = file2byte("/home/aimir1/aimiramm/penta/soria_certs/hes_penta.key");
    Integer prikey_buffer_pki_len = prikey_buffer_pki.length;
    byte[] cert_buffer_pki = file2byte("/home/aimir1/aimiramm/penta/soria_certs/000B120000000040.der");
    Integer cert_buffer_pki_len = cert_buffer_pki.length;
    byte[] passwd = "4d1deb6066ca854fc0c0a9187c3c207eb3ed9a7f".getBytes();;
    Integer passwd_len = passwd.length;

    ret = iNuriServer.IS_IoT_PKI_Encrypt
      (plain_pki, plain_pki_len
      , cert_buffer_pki, cert_buffer_pki_len
      , prikey_buffer_pki, prikey_buffer_pki_len
      , passwd, passwd_len
      , cipher_pki, cipher_pki_len);


    System.out.println("JAVA - cipher_pki:" + PrintHex(cipher_pki,cipher_pki_len));
    System.out.println("JAVA - cipher_pki_len:" + cipher_pki_len);

  System.out.println("JAVA - -------------IS_IoT_PKI_Encrypt End-----------------");

  System.out.println("JAVA - -------------IS_IoT_PKI_Decrypt Start at Client -----------------");

    byte[] plain = new byte[1000];
    Integer plain_len = plain.length;
    //cipher_pki = hexToByteArray("B01C83648BA767AD9AD8333FB0D6B7476A987ED27D59BBCCF4C355F55AAB6BC8F9EEC4A5FA59ECE7C8D9AF2A6179B1D2407AC8B39DF7735C9ABC4A99A271E0A4");
    cipher_pki = hexToByteArray("C026C0001E50F12ED086162D4324C546D0BFE29B10BE114A20DB8DC67A13778C100B07270A6864720E9F20C771C3339378DF5A65CDEC658B882DC8E91A3463D6");
    cipher_pki_len = cipher_pki.length;
    byte[] pincode_c = "ga77ln71yew2kz8l".getBytes();
    Integer pincode_len_c = pincode_c.length;
    byte[] cinfo = "000B120000000040".getBytes();
    Integer cinfo_len = cinfo.length;
    byte[] salt = "rvfv24hzv8vx8o61".getBytes();
    Integer salt_len = salt.length;
    prikey_buffer_pki = file2byte("/home/aimir1/aimiramm/penta/soria_certs/000B120000000040.key");
    prikey_buffer_pki_len = prikey_buffer_pki.length;
    cert_buffer_pki = file2byte("/home/aimir1/aimiramm/penta/soria_certs/hes_penta.der");
    cert_buffer_pki_len = cert_buffer_pki.length;

    ret = iNuriClient.IS_IoT_PKI_Decrypt_forClient
      (plain, plain_len
      , cipher_pki, cipher_pki_len
      , pincode_c, pincode_len_c
      , cinfo, cinfo_len
      , salt, salt_len 
      , prikey_buffer_pki, prikey_buffer_pki_len
      , cert_buffer_pki, cert_buffer_pki_len);

    System.out.println("JAVA - plain:" + PrintHex(plain,plain_len));
    System.out.println("JAVA - plain_len:" + plain_len);
    System.out.println("JAVA - ret:" + ret);


  System.out.println("JAVA - -------------IS_IoT_PKI_Decrypt End-----------------");

  }//main
}
