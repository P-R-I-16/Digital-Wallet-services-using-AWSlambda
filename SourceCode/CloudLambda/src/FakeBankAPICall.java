
class FakeBankAPICall {

	public static boolean verifyBankAcct(BankAcct acctDtls) {
		boolean flag=false;
		if(acctDtls.getBankName().equals("HDFC"))
			flag= true;
		return flag;
	}
}
