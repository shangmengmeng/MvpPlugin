package template;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MVPPluginAction extends AnAction {
    Project project;
    VirtualFile selectFolder;//所选择的文件夹
    String layoutPath;//布局所在文件夹

    @Override
    public void actionPerformed(AnActionEvent e) {
        project = e.getProject();
        selectFolder =  CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
       // layoutPath = project.getBasePath()+"\\app\\src\\main\\res\\layout";
        layoutPath = project.getBasePath()+"\\app\\src\\main\\res\\layouts\\activity\\layout";//这个项目变态没有改回来
        String className = Messages.showInputDialog(project, "请输入类名", "MVP", Messages.getInformationIcon());
        if (className == null || className.equals("")) {
            System.out.print("请输入类名");
        } else {
            createMvpClass(className);
        }

    }

    /**
     * 创建各个类文件
     */
    private void createMvpClass(String className) {

        String path = selectFolder.getPath();
        String packageName = path.substring(path.indexOf("java") + 5, path.length()).replace("/", ".");
        String[] strings = packageName.split("\\.");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (i < 3)
                stringBuilder.append(strings[i]+".");
        }
        String basePackageName = stringBuilder.substring(0,stringBuilder.length()-1);
        System.out.print(packageName);
        System.out.print(basePackageName);
        //Contract文件创建
        String contract = readFile("Contract")
                .replace("&package&", packageName)
                .replace("&basePackageName&", basePackageName)
                .replace("&Contract&", className + "Contract");
        writetoFile(contract, path+"/contract", className + "Contract.java");
        //Presenter文件创建
        String presenter = readFile("Presenter")
                .replace("&package&", packageName)
                .replace("&basePackageName&", basePackageName)
                .replace("&Contract&", className + "Contract")
                .replace("&Presenter&",className+"Presenter");
        writetoFile(presenter,path+"/presenter",className+"Presenter.java");
        //Model文件
        String model = readFile("Model")
                .replace("&package&", packageName)
                .replace("&basePackageName&", basePackageName)
                .replace("&Contract&", className + "Contract")
                .replace("&Presenter&",className+"Presenter")
                .replace("&Model&",className+"Model")
                ;
        writetoFile(model,path+"/model",className+"Model.java");
        //
        boolean isFragment = className.endsWith("Fragment") || className.endsWith("fragment");
        //创建Activity
        if (!isFragment){
            String xmlName ="activity_" + camelToUnderline(className.substring(0,className.length() - 8));
            String activity = readFile("Activity")
                    .replace("&package&", packageName)
                    .replace("&basePackageName&", basePackageName)
                    .replace("&Contract&", className + "Contract")
                    .replace("&Presenter&",className+"Presenter")
                    .replace("&Activity&",className)
                    .replace("$xml&",xmlName)
                    ;
            writetoFile(activity,path+"/view",className+".java");
            //创建xml
            String layout = readFile("Layout");

            writetoFile(layout,layoutPath,xmlName+".xml");

        }



    }

    private String readFile(String filename) {
        InputStream in = null;
        in = this.getClass().getResourceAsStream("code/" + filename);
        String content = "";
        try {
            content = new String(readStream(in));
        } catch (Exception e) {
        }
        return content;
    }

    private void writetoFile(String content, String filepath, String filename) {
        try {
            File floder = new File(filepath);
            // if file doesnt exists, then create it
            if (!floder.exists()) {
                floder.mkdirs();
            }
            File file = new File(filepath + "/" + filename);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
                System.out.println(new String(buffer));
            }

        } catch (IOException e) {
        } finally {
            outSteam.close();
            inStream.close();
        }
        return outSteam.toByteArray();
    }

    /**
     * java 大写转小写加横杠
     */
    public  String camelToUnderline(String param){
        Pattern p=Pattern.compile("[A-Z]");
        if(param==null ||param.equals("")){
            return "";
        }
        StringBuilder builder=new StringBuilder(param);
        Matcher mc=p.matcher(param);
        int i=0;
        while(mc.find()){
            builder.replace(mc.start()+i, mc.end()+i, "_"+mc.group().toLowerCase());
            i++;
        }

        if('_' == builder.charAt(0)){
            builder.deleteCharAt(0);
        }
        return builder.toString();
    }
}
