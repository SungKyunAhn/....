import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEInputDecryptorProviderBuilder;

/**
 * 
 */

/**
 * @author sjlee
 *
 */
public class KeystoreSample {

	/**
	* @param args
	*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//System.out.println("Hello world");
		Security.addProvider(new BouncyCastleProvider());
		
		// arg0 : keyfile path and name
		// arg1 : keyfile password
		// arg2 : key alias
		// arg3 : certfile path and name
		// arg4 : keystore path and name
		try {
			// Read Private from Keyfile
			PrivateKey priKey = null;
			// String keyFile = "/home/aimir1/aimiramm/penta/keystore_test/cert5.key"; // Keyfile path
			// String passwd = "1234"; // Keyfile password (PKCS#5)
			String keyFile = args[0];
			String passwd = args[1];
			RandomAccessFile keyfile = new RandomAccessFile(new File(keyFile), "r");
			byte[] keydata = new byte[(int)keyfile.length()];
			keyfile.readFully(keydata);
			keyfile.close();
			keyfile = null;
	       
	        PKCS8EncryptedPrivateKeyInfo priv = new PKCS8EncryptedPrivateKeyInfo(keydata);
			PrivateKeyInfo priKeyInfo = priv.decryptPrivateKeyInfo(
					new JcePKCSPBEInputDecryptorProviderBuilder().setProvider("BC").build(passwd.toCharArray()));

			byte[] keyBytes = priKeyInfo.getEncoded();
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory kf = KeyFactory.getInstance(args[5], "BC");
			// KeyFactory kf = KeyFactory.getInstance("EC", "BC");
			priKey = kf.generatePrivate(spec);
			
			//------------------------------------------------------------------------
			
			// Import private key into Keystore

			// Load the keystore
			// String KeystoreFilename = "/home/aimir1/aimiramm/penta/keystore_test/jks_keystore"; // keystore file path
			String KeystoreFilename = args[4];
			String keyStorePassword = "aimiramm";
			// create keystore
            KeyStore keyStore =  null;
			try {
                keyStore = KeyStore.getInstance("jks");
                FileInputStream keyStoreInputStream = new FileInputStream(KeystoreFilename);
                keyStore.load(keyStoreInputStream, keyStorePassword.toCharArray());
                keyStoreInputStream.close();
            }
            catch(Exception e) {
                keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                char[] password = "some password".toCharArray();
                keyStore.load(null, password);
                FileOutputStream fos = new FileOutputStream(KeystoreFilename);
                keyStore.store(fos, password);
                fos.close();
            }

			// import cert
			// String certfile = "/home/aimir1/aimiramm/penta/keystore_test/cert5.der"; // cert file path
			String certfile = args[3];
			FileInputStream certificateStream = new FileInputStream(certfile);
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			java.security.cert.Certificate[] chain = {};
			chain = certificateFactory.generateCertificates(certificateStream).toArray(chain);
			certificateStream.close();

			// import prikey
			String privateKeyEntryPassword = "aimiramm"; // keystore에 개인키를 저장할때 사용할 PW
			// String entryAlias = "prikeyalias";
			String entryAlias = args[2];
			keyStore.setEntry(entryAlias, new KeyStore.PrivateKeyEntry(priKey, chain),
			        new KeyStore.PasswordProtection(privateKeyEntryPassword.toCharArray()));

			// Write out the keystore
			FileOutputStream keyStoreOutputStream = new FileOutputStream(KeystoreFilename);
			keyStore.store(keyStoreOutputStream, keyStorePassword.toCharArray());
			keyStoreOutputStream.close();
		}
		catch(Exception e) {
		    e.printStackTrace();
		}
	}	
}

 
