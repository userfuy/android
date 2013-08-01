package com.fuyong.main;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-7-22
 * Time: 下午9:20
 * To change this template use File | Settings | File Templates.
 */
public class HttpDownload {
    Logger log = Log.getLogger(Log.MY_APP);
    private ExecutorService executor;
    private int threadCount = 1;

    private String url;
    private String localPath;
    private String fileName;
    private int fileSize;

    private List<DownloadTask> downloadTasks = new ArrayList<DownloadTask>();
    private File saveFile;

    Boolean stopFlag = new Boolean(false);

    public HttpDownload(String url, String localPath) {
        this.url = url;
        this.localPath = localPath;
    }

    public HttpDownload(String url, String localPath, int threadCount) {
        this.url = url;
        this.localPath = localPath;
        this.threadCount = threadCount;
        if (this.threadCount < 1) {
            this.threadCount = 1;
        }
    }

    public boolean download() {
        log.info("begin download:" + url + ", thread count:" + threadCount);
        synchronized (stopFlag) {
            stopFlag = false;
        }
        this.saveFile = createLocalFile();
        if (null == this.saveFile) {
            log.info("create local file failed:" + localPath + fileName);
            return false;
        }
        if (!createDownloadCfg()) {
            log.info("create download config failed");
            return false;
        }
        if (!parseDownloadCfg()) {
            log.info("parse download config failed");
            return false;
        }

        executor = Executors.newFixedThreadPool(threadCount);
        CompletionService<DownloadTask> completionService = new ExecutorCompletionService<DownloadTask>(executor);
        synchronized (stopFlag) {
            if (stopFlag) {
                return false;
            }
            for (DownloadTask task : downloadTasks) {
                completionService.submit(task);
            }
        }

        RandomAccessFile cfg = null;
        long taskDownloadSizeOffset = 0;
        try {
            cfg = new RandomAccessFile(getDownloadCfgPath(), "rw");
            taskDownloadSizeOffset = cfg.length() - 4 * threadCount;
            int taskCount = threadCount;
            while (taskCount > 0) {
                Future<DownloadTask> future = completionService.take();
                DownloadTask downloadTask = future.get();
                cfg.seek(taskDownloadSizeOffset + 4 * downloadTask.getTaskId());
                cfg.writeInt(downloadTask.getDownload());
                // 下载未停止且任务未完成，则重新提交任务
                synchronized (stopFlag) {
                    if (!stopFlag) {
                        if (!downloadTask.isDownloadSuc()) {
                            completionService.submit(downloadTask);
                            ++taskCount;
                        }
                    }
                }
                --taskCount;
            }
            executor.shutdown();
            executor.awaitTermination(60, TimeUnit.SECONDS);
            if (!executor.isTerminated()) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Log.exception(e);
        } catch (FileNotFoundException e) {
            Log.exception(e);
        } catch (ExecutionException e) {
            Log.exception(e);
        } catch (IOException e) {
            Log.exception(e);
        } finally {
            try {
                if (cfg != null) {
                    cfg.close();
                }
            } catch (IOException e) {
                Log.exception(e);
            }
        }
        // 下载成功，删除下载配置
        boolean success = isDownloadSuc();
        if (success) {
            new File(getDownloadCfgPath()).delete();
        } else {
            saveDownloadCfg();
        }
        log.info("end download. result:" + success);
        return success;
    }

    public void stop() {
        log.info("stop download");
        synchronized (stopFlag) {
            stopFlag = true;
        }
        if (null != executor) {
            executor.shutdownNow();
        }
    }

    public int downloadSize() {
        int downloadSize = 0;
        for (DownloadTask task : downloadTasks) {
            downloadSize += task.getDownload();
        }
        return downloadSize;
    }

    private File createLocalFile() {
        HttpURLConnection conn = httpConnect(this.url, "GET");
        if (null == conn) {
            return null;
        }
        this.fileName = getFileName(conn, this.url);
        this.fileSize = conn.getContentLength();
        conn.disconnect();

        if (fileSize <= 0) {
            log.warn("file size:" + fileSize);
            return null;
        }

        File path = new File(this.localPath);
        if (!path.exists() && !path.mkdirs()) {
            return null;
        }
        RandomAccessFile randOut = null;
        try {
            File file = new File(this.localPath + File.separator + this.fileName);
            if (!file.exists() && !file.createNewFile()) {
                return null;
            }
            randOut = new RandomAccessFile(file, "rw");
            randOut.setLength(this.fileSize);
            return file;
        } catch (FileNotFoundException e) {
            Log.exception(e);
            Log.exception(e);
        } catch (IOException e) {
            Log.exception(e);
        } finally {
            try {
                if (randOut != null) {
                    randOut.close();
                }
            } catch (IOException e) {
                Log.exception(e);
            }
        }
        return null;
    }

    private boolean isDownloadSuc() {
        return fileSize == downloadSize();
    }

    private void saveDownloadCfg() {
        RandomAccessFile cfg;
        long taskDownloadSizeOffset;
        try {
            cfg = new RandomAccessFile(getDownloadCfgPath(), "rw");
            taskDownloadSizeOffset = cfg.length() - 4 * threadCount;
            for (DownloadTask task : downloadTasks) {
                cfg.seek(taskDownloadSizeOffset + 4 * task.getTaskId());
                cfg.writeInt(task.getDownload());
            }
        } catch (FileNotFoundException e) {
            Log.exception(e);
        } catch (IOException e) {
            Log.exception(e);
        }
    }

    private boolean createDownloadCfg() {
        File downloadCfg = new File(getDownloadCfgPath());
        if (downloadCfg.exists()) {
            return true;
        }
        try {
            downloadCfg.createNewFile();
        } catch (IOException e) {
            Log.exception(e);
            return false;
        }
        DataOutputStream dataOutputStream = null;
        try {
            dataOutputStream = new DataOutputStream(new FileOutputStream(downloadCfg));
            dataOutputStream.writeInt(0x0001);
            byte[] bytes = url.getBytes("UTF-16BE");
            dataOutputStream.writeInt(bytes.length);
            dataOutputStream.write(bytes);
            dataOutputStream.writeInt(0x0002);
            dataOutputStream.writeInt(8 + 4 * threadCount);
            dataOutputStream.writeInt(fileSize);
            dataOutputStream.writeInt(threadCount);
            for (int i = 0; i < threadCount; ++i) {
                // download
                dataOutputStream.writeInt(0);
            }
            dataOutputStream.close();

            return true;
        } catch (FileNotFoundException e) {
            Log.exception(e);
        } catch (IOException e) {
            Log.exception(e);
        }
        return false;
    }

    private boolean parseDownloadCfg() {
        File cfg = new File(getDownloadCfgPath());
        if (!cfg.exists()) {
            return false;
        }
        try {
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(cfg));
            int tagUrl = dataInputStream.readInt();
            if (0x0001 != tagUrl) {
                return false;
            }
            int urlLen = dataInputStream.readInt();
            byte[] bytes = new byte[urlLen];
            dataInputStream.read(bytes);
            url = new String(bytes, "UTF-16BE");
            int tagTasks = dataInputStream.readInt();
            if (0x0002 != tagTasks) {
                return false;
            }
            int tasksLen = dataInputStream.readInt();
            int fileSize = dataInputStream.readInt();
            // 下载配置文件记录的文件大小与新获取的不一致，新建下载配置
            if (fileSize != this.fileSize) {
                cfg.delete();
                createDownloadCfg();
                return parseDownloadCfg();
            }
            threadCount = dataInputStream.readInt();
            int blockSize = (fileSize + threadCount - 1) / threadCount;
            for (int i = 0; i < threadCount - 1; ++i) {
                downloadTasks.add(new DownloadTask(i, i * blockSize, blockSize, dataInputStream.readInt()));
            }
            int start = (threadCount - 1) * blockSize;
            downloadTasks.add(new DownloadTask(threadCount - 1, start, fileSize - start, dataInputStream.readInt()));
            dataInputStream.close();

            return true;
        } catch (FileNotFoundException e) {
            Log.exception(e);
        } catch (IOException e) {
            Log.exception(e);
        }
        return false;
    }

    private String getDownloadCfgPath() {
        return localPath + fileName + ".download";
    }

    private String getFileName(HttpURLConnection conn, String url) {
        String filename = url.substring(url.lastIndexOf('/') + 1);

        if (filename == null || "".equals(filename.trim())) {
            //如果获取不到文件名称
            for (int i = 0; ; i++) {
                String mine = conn.getHeaderField(i);
                if (mine == null) break;

                if ("content-disposition".equals(conn.getHeaderFieldKey(i).toLowerCase())) {
                    Matcher m = Pattern.compile(".*filename=(.*)").matcher(mine.toLowerCase());
                    if (m.find()) {
                        return m.group(1);
                    }
                }
            }

            filename = UUID.randomUUID() + ".tmp";//默认取一个文件名
        }
        return filename;
    }

    private HttpURLConnection httpConnect(String httpUrl, String method) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(httpUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15 * 1000);
            conn.setRequestMethod(method);
            conn.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
//            conn.setRequestProperty("Accept-Language", "zh-CN");
            conn.setRequestProperty("Referer", httpUrl);
            conn.setRequestProperty("Charset", "UTF-8");
//            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
            conn.setRequestProperty("Connection", "Keep-Alive");
            // connect后，不能再设置请求属性
//            conn.connect();
            return conn;
        } catch (MalformedURLException e) {
            Log.exception(e);
        } catch (ProtocolException e) {
            Log.exception(e);
        } catch (IOException e) {
            Log.exception(e);
        }
        return null;
    }

    public class DownloadTask implements Callable<DownloadTask> {
        private int taskId;
        private int start;
        private int block;
        private int download;

        public DownloadTask(int taskId, int start, int block, int offset) {
            this.taskId = taskId;
            this.start = start;
            this.block = block;
            download = offset;
        }

        public boolean isDownloadSuc() {
            return download == block;
        }

        public int getTaskId() {
            return taskId;
        }

        public int getDownload() {
            return download;
        }

        @Override
        public DownloadTask call() {
            HttpURLConnection http = null;
            http = httpConnect(HttpDownload.this.url, "GET");
            if (null == http) {
                return this;
            }
            int startPos = start + download;//开始位置
            int endPos = start + block - 1;//结束位置
            http.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);//设置获取实体数据的范围

            InputStream inStream = null;
            RandomAccessFile randomAccessFile = null;
            try {
                inStream = http.getInputStream();
                randomAccessFile = new RandomAccessFile(HttpDownload.this.saveFile, "rwd");
                randomAccessFile.seek(startPos);

                byte[] buffer = new byte[10240];
                int readLen = 0;
                while ((readLen = inStream.read(buffer, 0, 10240)) != -1) {
                    randomAccessFile.write(buffer, 0, readLen);
                    download += readLen;
                    // 停止下载
                    synchronized (stopFlag) {
                        if (stopFlag) {
                            break;
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                Log.exception(e);
            } catch (IOException e) {
                Log.exception(e);
            } finally {
                try {
                    if (inStream != null) {
                        inStream.close();
                    }
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                    }
                } catch (IOException e) {
                    Log.exception(e);
                }
                if (http != null) {
                    http.disconnect();
                }
            }

            return this;
        }
    }
}
