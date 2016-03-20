package gr.watchful.permchecker.utils;

import gr.watchful.permchecker.datastructures.Globals;
import org.json.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;

public class Updater {
    public static String checkUpdate(String currentVersion) {
        String latestJSON = FileUtils.downloadToString(Globals.latestReleaseUrl);
        try {
            JSONObject latest = new JSONObject(latestJSON);
            String latestVersion = latest.getString("tag_name");

            if (versionCompare(currentVersion, latestVersion) < 0) {
                JSONArray assets = latest.getJSONArray("assets");
                for(int i=0; i<assets.length(); i++) {
                    if (assets.getJSONObject(i).getString("name").equals("PermChecker.jar")) {
                        return assets.getJSONObject(i).getString("browser_download_url");
                    }
                }
            }
            return null;
        } catch (JSONException e) {
            System.out.println("Couldn't parse json for update");
            return null;
        }
    }

    public static void startUpdate(String versionURL) {
        StringBuilder bldr = new StringBuilder();
        File logFile = new File("StartUpdate.txt");
        try {
            logFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File currentJar = getCurrentJar();
        bldr.append("Current Jar: "+currentJar.getAbsolutePath()+"\n");
        File parentFolder = new File(currentJar.getParent());
        bldr.append("Parent Folder: "+parentFolder.getAbsolutePath()+"\n");

        File newJar = new File(parentFolder+File.separator+"NewPermChecker.jar");
        bldr.append("New Jar: "+newJar.getAbsolutePath()+"\n");
        try {
            FileUtils.downloadToFile(new URL(versionURL), newJar);
        } catch (IOException e) {
            System.out.println("Couldn't download update");
            bldr.append("Couldn't download update"+"\n");
            FileUtils.writeFile(bldr.toString(), logFile);
            return;
        }

        File updaterJar = new File(parentFolder+File.separator+"UpdaterPermChecker.jar");
        bldr.append("Updater Jar: "+updaterJar.getAbsolutePath()+"\n");

        FileUtils.copyFile(newJar, updaterJar);
        bldr.append("Copied to updater"+"\n");

        String[] run = {"java","-jar",updaterJar.getName(),"-u",currentJar.getAbsolutePath(),newJar.getAbsolutePath()};
        for (String cmd : run) {
            bldr.append(cmd+" ");
        }
        bldr.append("\n");
        FileUtils.writeFile(bldr.toString(), logFile);
        try {
            Runtime.getRuntime().exec(run);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }

    public static void finishUpdate(String targetFileString, String sourceFileString) {
        StringBuilder bldr = new StringBuilder();
        File logFile = new File("FinishUpdate.txt");

        try {
            new File("PING1").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            logFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            new File("PING2").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File targetFile = new File(targetFileString);
        bldr.append("Target Jar: "+targetFile.getAbsolutePath()+"\n");
        if (!targetFile.exists()) {
            System.out.println("Updater, target file does not exist, aborting");
            bldr.append("Updater, target file does not exist, aborting"+"\n");
            FileUtils.writeFile(bldr.toString(), logFile);
            return;
        }

        try {
            new File("PING3").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }


        File sourceFile = new File(sourceFileString);
        bldr.append("Source Jar: "+sourceFile.getAbsolutePath()+"\n");
        if (!sourceFile.exists()) {
            System.out.println("Updater, source file does not exist, aborting");
            bldr.append("Updater, source file does not exist, aborting"+"\n");
            FileUtils.writeFile(bldr.toString(), logFile);
            return;
        }

        try {
            new File("PING4").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }


        targetFile.delete();
        bldr.append("Deleted target" + "\n");
        sourceFile.renameTo(targetFile);
        bldr.append("Copied source" + "\n");


        try {
            new File("PING5").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File updaterFile = getCurrentJar();
        bldr.append("Updater Jar: "+updaterFile.getAbsolutePath()+"\n");


        try {
            new File("PING6").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] run = {"java","-jar",targetFile.getName(),"-c",sourceFile.getAbsolutePath(),updaterFile.getAbsolutePath()};
        for (String cmd : run) {
            bldr.append(cmd+" ");
        }
        bldr.append("\n");

        try {
            new File("PING7").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileUtils.writeFile(bldr.toString(), logFile);
        try {
            Runtime.getRuntime().exec(run);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }

    public static void cleanup(String fileString1, String fileString2) {
        StringBuilder bldr = new StringBuilder();
        File logFile = new File("CleanUpdate.txt");
        try {
            logFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(fileString1);
        if (!file.exists()) {
            System.out.println("file 1 does not exist");
            bldr.append("file 1 does not exist" + "\n");
        }
        file.delete();
        bldr.append("deleted 1" + "\n");

        file = new File(fileString2);
        if (!file.exists()) {
            System.out.println("file 2 does not exist");
            bldr.append("file 2 does not exist" + "\n");
        }
        file.delete();
        bldr.append("deleted 2" + "\n");
        FileUtils.writeFile(bldr.toString(), logFile);
    }

    public static File getCurrentJar() {
        String path;
        try {
            path = Updater.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        } catch (URISyntaxException e) {
            System.out.println("Couldn't get path");
            return null;
        }
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("Couldn't get path");
            return null;
        }
        return new File(path);
    }

    public static Integer versionCompare(String str1, String str2)
    {
        String[] vals1 = str1.split("\\.");
        String[] vals2 = str2.split("\\.");
        int i = 0;
        while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i]))
        {
            i++;
        }
        if (i < vals1.length && i < vals2.length)
        {
            int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
            return Integer.signum(diff);
        }
        else
        {
            return Integer.signum(vals1.length - vals2.length);
        }
    }
}
