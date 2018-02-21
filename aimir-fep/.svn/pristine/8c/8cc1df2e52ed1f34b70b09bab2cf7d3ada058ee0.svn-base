

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

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
public class PANA_KeystoreSample {

	/**
	* @param args
	*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//System.out.println("Hello world");
		Security.addProvider(new BouncyCastleProvider());
		
		try {
			// Read Private from Keyfile
			PrivateKey priKey = null;
			String keyFile = "/home/aimir/aimiramm/penta/ECDSA/pana/hes_pana_key.der"; // Keyfile path
			RandomAccessFile keyfile = new RandomAccessFile(new File(keyFile), "r");
			byte[] keydata = new byte[(int)keyfile.length()];
			keyfile.readFully(keydata);
			keyfile.close();
			keyfile = null;
			
			PrivateKeyInfo priv = PrivateKeyInfo.getInstance(keydata);
	        
	        byte[] keyBytes = priv.getEncoded();
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory kf = KeyFactory.getInstance("EC", "BC"); 
			priKey = kf.generatePrivate(spec);
	         
			//------------------------------------------------------------------------
			
			// Import private key into Keystore

			// Load the keystore
			String KeystoreFilename = "/home/aimir/aimiramm/penta/ECDSA/aimir_keystore.jks"; // keystore file path 
			String keyStorePassword = "aimiramm";
			KeyStore keyStore = KeyStore.getInstance("jks");
			FileInputStream keyStoreInputStream = new FileInputStream(KeystoreFilename);
			keyStore.load(keyStoreInputStream, keyStorePassword.toCharArray());
			keyStoreInputStream.close();

			// import cert
			String certfile = "/home/aimir/aimiramm/penta/ECDSA/pana/hes_pana.der"; // cert file path
			FileInputStream certificateStream = new FileInputStream(certfile);
			
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			java.security.cert.Certificate[] chain = {};
			chain = certificateFactory.generateCertificates(certificateStream).toArray(chain);
			certificateStream.close();

			// import prikey
			String privateKeyEntryPassword = "aimir"; // keystore�뿉 媛쒖씤�궎瑜� ���옣�븷�븣 �궗�슜�븷 PW
			String entryAlias = "panaaimirkey";
			keyStore.setEntry(entryAlias, new KeyStore.PrivateKeyEntry(priKey, chain), new KeyStore.PasswordProtection(privateKeyEntryPassword.toCharArray()));

			// Write out the keystore
			FileOutputStream keyStoreOutputStream = new FileOutputStream(KeystoreFilename);
			keyStore.store(keyStoreOutputStream, keyStorePassword.toCharArray());
			keyStoreOutputStream.close();
		}
		catch(Exception e) {
			System.out.println("Exception : " + e.toString());
		}
	}	
}

 
