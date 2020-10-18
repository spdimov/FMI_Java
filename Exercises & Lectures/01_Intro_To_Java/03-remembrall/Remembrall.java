public class Remembrall {
    public static boolean isPhoneNumberForgettable(String phoneNumber){
        if( phoneNumber == null){
            return false;
        }
        if(phoneNumber.isBlank()){
            return false;
        }
        boolean isForgettable=true;
        char[] number=phoneNumber.toCharArray();
        for(int i=0;i<phoneNumber.length();i++){
            if(number[i] != '-' && number[i] != ' ' && number[i] != '(' && number[i] != ')') {
                if (Character.isLetter(number[i])) {
                    return true;
                }

                if (i != phoneNumber.toString().lastIndexOf(number[i])) {
                    isForgettable=false;
                }
            }
        }
        return isForgettable;
    }
}

