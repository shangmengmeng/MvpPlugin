package template;

public class Pig {
    public static void main(String[] args) {
        String pig="com.intellij.openapi.actionSystem.AnAction";
        System.out.println("pig---->"+pig);
        String[] strings = pig.split("\\.");
        System.out.println("pig---->"+strings);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (i < 3){
                stringBuilder.append(strings[i]+".");
            }

        }
        System.out.println("pig---->"+stringBuilder.substring(0,stringBuilder.length()-1));
    }
}
