package com.aimir.fep.protocol.security;


/**
 * Refer from "PENTASECURITY_SECURITY_MODULE_ERRORCODE_20160810.xlsx"
 */
public class ISIoTErrorCode {

    // IS_IoT_Verify_Init_Token
    public enum Verify_Init_Token{
        NORMAL(0),
        Init_Token_Is_Null(-1);

        private int retCode;

        Verify_Init_Token(int retCode){
            this.retCode = retCode;
        }

        public String getErrorMessage() {
            return this.name();
        }

        public int getCode(){
            return this.retCode;
        }

        public String getErrorMessage(int retCode){

            for (Verify_Init_Token type : Verify_Init_Token.values()) {
                if (type.getCode() == retCode)
                    return type.name();
            }

            return "Unknown["+retCode+"]";
        }
    }

    // IS_IoT_Make_OrgMsg_For_Stoken
    public enum Make_OrgMsg_For_Stoken{
        NORMAL(0),
        Parameter_Is_Null(-1);

        private int retCode;

        Make_OrgMsg_For_Stoken(int retCode){
            this.retCode = retCode;
        }

        public String getErrorMessage() {
            return this.name();
        }

        public int getCode(){
            return this.retCode;
        }

        public String getErrorMessage(int retCode){

            for (Make_OrgMsg_For_Stoken type : Make_OrgMsg_For_Stoken.values()) {
                if (type.getCode() == retCode)
                    return type.name();
            }

            return "Unknown["+retCode+"]";
        }
    }

    // IS_IoT_SignedData_For_Stoken
    public enum SignedData_For_Stoken {
        NORMAL(0),
        Invalid_PrivateKeyFile_Data(-1),
        ECC_Signature_Type_Is_Unsupportable(-2),
        MemoryFail_By_Wrong_Certificate_Password(-3), //memory allocation failure
        SignatureFail_By_Wrong_Certificate_Password(-4);  //signature failure

        private int retCode;

        SignedData_For_Stoken(int retCode){
            this.retCode = retCode;
        }

        public String getErrorMessage() {
            return this.name();
        }

        public int getCode(){
            return this.retCode;
        }

        public String getErrorMessage(int retCode){

            for (SignedData_For_Stoken type : SignedData_For_Stoken.values()) {
                if (type.getCode() == retCode)
                    return type.name();
            }

            return "Unknown["+retCode+"]";
        }

    }

    // IS_IoT_Make_Stoken_With_Sigdata
    public enum Make_Stoken_With_Sigdata{
        NORMAL(0),
        Invalid_CertificateFile_Data(-1);

        private int retCode;

        Make_Stoken_With_Sigdata(int retCode){
            this.retCode = retCode;
        }

        public String getErrorMessage() {
            return this.name();
        }

        public int getCode(){
            return this.retCode;
        }

        public String getErrorMessage(int retCode){

            for (Make_Stoken_With_Sigdata type : Make_Stoken_With_Sigdata.values()) {
                if (type.getCode() == retCode)
                    return type.name();
            }

            return "Unknown["+retCode+"]";
        }
    }

    // IS_IoT_Make_Stoken (ER code is same with IS_IoT_SignedData_For_Stoken)
    public enum Make_Stoken{
        NORMAL(0),
        Invalid_PrivateKeyFile_Data(-1),
        ECC_Signature_Type_Is_Unsupportable(-2),
        MemoryFail_By_Wrong_Certificate_Password(-3), //memory allocation failure
        SignatureFail_By_Wrong_Certificate_Password(-4),  //signature failure
        Invalid_CertificateFile_Data(-5);

        private int retCode;

        Make_Stoken(int retCode){
            this.retCode = retCode;
        }

        public String getErrorMessage() {
            return this.name();
        }

        public int getCode(){
            return this.retCode;
        }

        public String getErrorMessage(int retCode){

            for (Make_Stoken type : Make_Stoken.values()) {
                if (type.getCode() == retCode)
                    return type.name();
            }

            return "Unknown["+retCode+"]";
        }

    }


    // IS_IoT_Verify_Ctoken
	public enum Verify_Ctoken{
        NORMAL(0),
        Invalid_Ctoken_Data(-1),
        Invalid_Ctoken_Signature(-2),
        Ctoken_Extraction_Failure(-3),
        ECDSA_Extraction_Failure(-4),
        Privatekey_Not_Matching(-5),
        Invalid_CA_Cert_File(-6),
        Chain_Verify_Buffer_Init_Failure(-7),
        Chain_Verify_CA_Import_Failure(-8),
        Certificate_Not_Matching(-9),
        Chain_Verify_RootCA_Import_Failure(-10),
        CA_Path_Not_Matching(-11),
        CRL_Verify_Not_Possible(-12),
        BAD_Mutex_Operation(-106),
        CRL_Expired(-151),
        Ctoken_Certificate_Expired(-361),
        CRL_Missing(-362),
        Invalid_CRL_Data(-15);
		
        private int retCode;
        
        Verify_Ctoken(int retCode) {
            this.retCode = retCode;
        }
        
        public String getErrorMessage() {
            return this.name();
        }
        
        public int getCode(){
        	return this.retCode;
        }
        
        public String getErrorMessage(int retCode){
        	
            for (Verify_Ctoken type : Verify_Ctoken.values()) {
                if (type.getCode() == retCode)
                    return type.name();
            }

            return "Unknown["+retCode+"]";
        }
		
	}

    // IS_IoT_Get_Random
    public enum Get_Random {
        NORMAL(0),
        WOLFSSL_Error(-1);

        private int retCode;

        Get_Random(int retCode) {
            this.retCode = retCode;
        }

        public String getErrorMessage() {
            return this.name();
        }

        public int getCode(){
            return this.retCode;
        }

        public String getErrorMessage(int retCode){

            for (Get_Random type : Get_Random.values()) {
                if (type.getCode() == retCode)
                    return type.name();
            }
            if (retCode<0)
                return WOLFSSL_Error.name();

            return "Unknown["+retCode+"]";
        }
    }

    // IS_IoT_Encrypt
    public enum Encrypt {
        NORMAL(0),
        WOLFSSL_Error(-1);

        private int retCode;

        Encrypt(int retCode) {
            this.retCode = retCode;
        }

        public String getErrorMessage() {
            return this.name();
        }

        public int getCode(){
            return this.retCode;
        }

        public String getErrorMessage(int retCode){
            for (Encrypt type : Encrypt.values()) {
                if (type.getCode() == retCode)
                    return type.name();
            }
            if (retCode<0)
                return WOLFSSL_Error.name();

            return "Unknown["+retCode+"]";
        }
    }

    // IS_IoT_Decrypt
    public enum Decrypt {
        NORMAL(0),
        Cipher_Length_Error(-1),
        WOLFSSL_Error(-2);

        private int retCode;

        Decrypt(int retCode) {
            this.retCode = retCode;
        }

        public String getErrorMessage() {
            return this.name();
        }

        public int getCode(){
            return this.retCode;
        }

        public String getErrorMessage(int retCode){
            for (Decrypt type : Decrypt.values()) {
                if (type.getCode() == retCode)
                    return type.name();
            }
            if (retCode<0)
                return WOLFSSL_Error.name();

            return "Unknown["+retCode+"]";
        }
    }

    // IS_IoT_PKI_Encrypt
    public enum PKI_Encrypt {
        NORMAL(0),
        Parameter_Is_Null(-1),
        ECDSA_Extraction_Failure(-2),
        Wrong_Certificate_Password(-3),
        Not_Enough_Cipher_Buffer(-4);

        private int retCode;

        PKI_Encrypt(int retCode) {
            this.retCode = retCode;
        }

        public String getErrorMessage() {
            return this.name();
        }

        public int getCode(){
            return this.retCode;
        }

        public String getErrorMessage(int retCode){
            for (PKI_Encrypt type : PKI_Encrypt.values()) {
                if (type.getCode() == retCode)
                    return type.name();
            }

            return "Unknown["+retCode+"]";
        }
    }

    // IS_IoT_PKI_Decrypt
    public enum PKI_Decrypt {
        NORMAL(0),
        Null_Parameter_Or_Wrong_Password(-1),
        ECDSA_Extraction_Failure(-2),
        Not_Enough_Plain_Buffer(-3);

        private int retCode;

        PKI_Decrypt(int retCode) {
            this.retCode = retCode;
        }

        public String getErrorMessage() {
            return this.name();
        }

        public int getCode(){
            return this.retCode;
        }

        public String getErrorMessage(int retCode){
            for (PKI_Decrypt type : PKI_Decrypt.values()) {
                if (type.getCode() == retCode)
                    return type.name();
            }

            return "Unknown["+retCode+"]";
        }
    }


}
