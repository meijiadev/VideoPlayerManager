package com.example.videoplayermanager.bean;

import java.util.List;

/**
 *desc: 广告机等待界面的参数
 */
public class AwaitMessage
{
    /**
     * code : 1
     * msg : 默认节目
     * data : [{"file_path":"http://admin.beiduofenkeji.com/uploads/20200830/16fed49766f7c28a188a46951b8c49d1.jpg","name":"默认图片","type":1},{"file_path":"http://admin.beiduofenkeji.com/uploads/20200830/095a5f77b9bbb631c4dcc87262c22ab4.mp4","name":"默认节目","type":2}]
     */

    private String code;
    private String msg;
    private List<DataBean> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * file_path : http://admin.beiduofenkeji.com/uploads/20200830/16fed49766f7c28a188a46951b8c49d1.jpg
         * name : 默认图片
         * type : 1
         */

        private String file_path;
        private String name;
        private int type;

        public String getFile_path() {
            return file_path;
        }

        public void setFile_path(String file_path) {
            this.file_path = file_path;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
