package com.fuyong.main;

import it.sauronsoftware.ftp4j.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-6-20
 * Time: 下午11:32
 * To change this template use File | Settings | File Templates.
 */

public final class FTPUtil {
    private static Logger log = Log.getLogger(Log.MY_APP);

    private FTPUtil() {
    }

    /**
     * 创建FTP连接
     *
     * @param host     主机名或IP
     * @param port     ftp端口
     * @param username ftp用户名
     * @param password ftp密码
     * @return 一个客户端
     */
    public static FTPClient makeFtpConnection(String host, int port, String username, String password) {
        FTPClient client = new FTPClient();
        try {
            log.info("ftp connecting");
            client.connect(host, port);
            client.login(username, password);
            client.setPassive(true);
            client.setType(FTPClient.TYPE_BINARY);
        } catch (FTPException e) {
            log.error("ftp exception");
            return null;
        } catch (IOException e) {
            log.error("io exception");
            return null;
        } catch (FTPIllegalReplyException e) {
            log.error("ftp illegal reply exception");
            return null;
        }
        log.info("ftp connected");
        return client;
    }

    /**
     * FTP下载文件到本地一个文件夹,如果本地文件夹不存在，则创建必要的目录结构
     *
     * @param client     FTP客户端
     * @param remoteFile FTP文件
     * @param localPath  存的本地目录
     */
    public static void download(FTPClient client, String remoteFile, String localPath
            , FTPDataTransferListener dataTransferListener) {

        if (null == client) {
            return;
        }

        if (isExist(client, remoteFile) != FTPFile.TYPE_FILE) {
            log.warn(remoteFile + " is not exist");
            return;
        }
        File localFile = new File(localPath);
        if (!localFile.exists()) {
            localFile.mkdirs();
            try {
                localFile.createNewFile();
            } catch (IOException e) {
                log.error("new file:" + localPath);
                return;
            }
            if (!localFile.isFile()) {
                localPath = localPath + File.separator + new File(remoteFile).getName();
            }
        }

        localPath = PathToolkit.formatFilePath(localPath);
        try {
            client.download(remoteFile, new File(localPath), dataTransferListener);
            log.info("ftp download success");
            return;
        } catch (FTPException e) {
            log.error("ftp exception");
        } catch (FileNotFoundException e) {
            log.error("file not found exception");
        } catch (IOException e) {
            log.error("io exception");
        } catch (FTPDataTransferException e) {
            log.error("ftp data transfer exception");
        } catch (FTPIllegalReplyException e) {
            log.error("ftp illegal reply exception");
        } catch (FTPAbortedException e) {
            log.error("ftp aborted exception");
        }
    }


    /**
     * FTP上传本地文件到FTP的一个目录下
     *
     * @param client           FTP客户端
     * @param localFile        本地文件
     * @param remoteFolderPath FTP上传目录
     */
    public static void upload(FTPClient client, File localFile, String remoteFolderPath
            , FTPDataTransferListener dataTransferListener) {
        if (!localFile.exists() || !localFile.isFile()) {
            return;
        }
        remoteFolderPath = PathToolkit.formatPath4FTP(remoteFolderPath);
        try {
            log.info("begin ftp upload");
            mkdirs(client, remoteFolderPath);
            client.changeDirectory(remoteFolderPath);
            client.upload(localFile, dataTransferListener);
            client.changeDirectory("/");
            log.info("ftp upload success");
            return;
        } catch (FileNotFoundException e) {
            log.error("file not found");
        } catch (IOException e) {
            log.error("IO exception");
        } catch (FTPException e) {
            log.error("ftp exception");
        } catch (FTPDataTransferException e) {
            log.error("ftp data transfer exception");
        } catch (FTPAbortedException e) {
            log.error("ftp aborted exception");
        } catch (FTPIllegalReplyException e) {
            log.error("ftp illegal reply exception");
        }
        log.info("ftp upload failed");
    }

    public static void mkdirs(FTPClient client, String path) throws FTPException, IOException, FTPIllegalReplyException {
        if (path == null) {
            return;
        }
        client.changeDirectory("/");
        path = PathToolkit.formatPath4FTP(path);
        String[] dirs = path.split("/");
        for (int i = 0; i < dirs.length; i++) {
            if (!dirs[i].isEmpty()) {
                if (-1 == isExist(client, dirs[i])) {
                    client.createDirectory(dirs[i]);
                }
                client.changeDirectory(dirs[i]);
            }
        }
    }

    /**
     * FTP上传本地文件到FTP的一个目录下
     *
     * @param client       FTP客户端
     * @param localFile    本地文件路径
     * @param remoteFolder FTP上传目录
     */
    public static void upload(FTPClient client, String localFile
            , String remoteFolder, FTPDataTransferListener dataTransferListener) {
        upload(client, new File(localFile), remoteFolder, dataTransferListener);
    }

    static void abortDataTransfer(FTPClient client, boolean sendAbortCmd) {
        if (null == client)
            return;
        try {
            log.info("abort ftp data transfer");
            client.abortCurrentDataTransfer(sendAbortCmd);
            log.info("ftp aborted");
        } catch (IOException e) {
            log.error("io exception");
        } catch (FTPIllegalReplyException e) {
            log.error("ftp illegal reply exception");
        }
    }

    /**
     * 判断一个FTP路径是否存在，如果存在返回类型(FTPFile.TYPE_DIRECTORY=1、FTPFile.TYPE_FILE=0、FTPFile.TYPE_LINK=2)
     * 如果文件不存在，则返回一个-1
     *
     * @param client     FTP客户端
     * @param remotePath FTP文件或文件夹路径
     * @return 存在时候返回类型值(文件0, 文件夹1, 连接2)，不存在则返回-1
     */
    public static int isExist(FTPClient client, String remotePath) {
        remotePath = PathToolkit.formatPath4FTP(remotePath);
        FTPFile[] list;
        try {
            list = client.list(remotePath);
        } catch (Exception e) {
            return -1;
        }

        if (list.length > 1) {
            return FTPFile.TYPE_DIRECTORY;
        } else if (list.length == 1) {
            FTPFile f = list[0];
            if (f.getType() == FTPFile.TYPE_DIRECTORY) return FTPFile.TYPE_DIRECTORY;
            String _path = remotePath + "/" + f.getName();
            try {
                int y = client.list(_path).length;
                if (y == 1) return FTPFile.TYPE_DIRECTORY;
                else return FTPFile.TYPE_FILE;
            } catch (Exception e) {
                return FTPFile.TYPE_FILE;
            }
        }
        return -1;
    }

    /**
     * 关闭FTP连接，关闭时候像服务器发送一条关闭命令
     *
     * @param client FTP客户端
     * @return 关闭成功，或者链接已断开，或者链接为null时候返回true，通过两次关闭都失败时候返回false
     */

    public static boolean closeConnection(FTPClient client) {
        if (client == null) return true;
        if (client.isConnected()) {
            try {
                client.disconnect(true);
            } catch (Exception e) {
                try {
                    client.disconnect(false);
                } catch (Exception e1) {
                    log.error("close ftp connection failed");
                    return false;
                }
            }
        }
        log.info("ftp connection closed");
        return true;
    }
}

class PathToolkit {
    private PathToolkit() {
    }

    /**
     * 格式化文件路径，将其中不规范的分隔转换为标准的分隔符,并且去掉末尾的文件路径分隔符。
     * 本方法操作系统自适应
     *
     * @param path 文件路径
     * @return 格式化后的文件路径
     */
    public static String formatFilePath(String path) {
        String reg0 = "\\\\+";
        String reg = "\\\\+|/+";
        String temp = path.trim().replaceAll(reg0, "/");
        temp = temp.replaceAll(reg, "/");
        if (temp.length() > 1 && temp.endsWith("/")) {
            temp = temp.substring(0, temp.length() - 1);
        }
        temp = temp.replace('/', File.separatorChar);
        return temp;
    }

    /**
     * 格式化文件路径，将其中不规范的分隔转换为标准的分隔符
     * 并且去掉末尾的"/"符号(适用于FTP远程文件路径或者Web资源的相对路径)。
     *
     * @param path 文件路径
     * @return 格式化后的文件路径
     */
    public static String formatPath4FTP(String path) {
        String reg0 = "\\\\+";
        String reg = "\\\\+|/+";
        String temp = path.trim().replaceAll(reg0, "/");
        temp = temp.replaceAll(reg, "/");
        if (temp.length() > 1 && temp.endsWith("/")) {
            temp = temp.substring(0, temp.length() - 1);
        }
        return temp;
    }

    /**
     * 获取FTP路径的父路径，但不对路径有效性做检查
     *
     * @param path FTP路径
     * @return 父路径，如果没有父路径，则返回null
     */
    public static String getParentPath4FTP(String path) {
        String pp = new File(path).getParent();
        if (pp == null) return null;
        else return formatPath4FTP(pp);
    }
}
