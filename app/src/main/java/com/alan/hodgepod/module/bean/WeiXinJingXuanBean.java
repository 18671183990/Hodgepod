package com.alan.hodgepod.module.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 16-6-2.
 */
public class WeiXinJingXuanBean implements Parcelable, Serializable {

    @Override
    public String toString() {
        return "WeiXinJingXuanBean{" +
                "error_code=" + error_code +
                ", reason='" + reason + '\'' +
                ", result=" + result +
                '}';
    }

    private int error_code;
    private String reason;
    private ResultBean result;


    protected WeiXinJingXuanBean(Parcel in) {
        error_code = in.readInt();
        reason = in.readString();
        result = in.readParcelable(ResultBean.class.getClassLoader());
    }

    public static final Creator<WeiXinJingXuanBean> CREATOR = new Creator<WeiXinJingXuanBean>() {
        @Override
        public WeiXinJingXuanBean createFromParcel(Parcel in) {
            return new WeiXinJingXuanBean(in);
        }

        @Override
        public WeiXinJingXuanBean[] newArray(int size) {
            return new WeiXinJingXuanBean[size];
        }
    };

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(error_code);
        dest.writeString(reason);
        dest.writeParcelable(result, flags);
    }


    public static class ResultBean implements Parcelable, Serializable {

        private int pno;
        private int ps;
        private int totalPage;
        private List<ListBean> list;


        protected ResultBean(Parcel in) {
            pno = in.readInt();
            ps = in.readInt();
            totalPage = in.readInt();
            list = in.createTypedArrayList(ListBean.CREATOR);
        }

        public static final Creator<ResultBean> CREATOR = new Creator<ResultBean>() {
            @Override
            public ResultBean createFromParcel(Parcel in) {
                return new ResultBean(in);
            }

            @Override
            public ResultBean[] newArray(int size) {
                return new ResultBean[size];
            }
        };

        public int getPno() {
            return pno;
        }

        public void setPno(int pno) {
            this.pno = pno;
        }

        public int getPs() {
            return ps;
        }

        public void setPs(int ps) {
            this.ps = ps;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(pno);
            dest.writeInt(ps);
            dest.writeInt(totalPage);
            dest.writeTypedList(list);
        }


        public static class ListBean implements Parcelable, Serializable {

            private String firstImg;
            private String id;
            private String mark;
            private String source;
            private String title;
            private String url;

            protected ListBean(Parcel in) {
                firstImg = in.readString();
                id = in.readString();
                mark = in.readString();
                source = in.readString();
                title = in.readString();
                url = in.readString();
            }

            public static final Creator<ListBean> CREATOR = new Creator<ListBean>() {
                @Override
                public ListBean createFromParcel(Parcel in) {
                    return new ListBean(in);
                }

                @Override
                public ListBean[] newArray(int size) {
                    return new ListBean[size];
                }
            };

            public String getFirstImg() {
                return firstImg;
            }

            public void setFirstImg(String firstImg) {
                this.firstImg = firstImg;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getMark() {
                return mark;
            }

            public void setMark(String mark) {
                this.mark = mark;
            }

            public String getSource() {
                return source;
            }

            public void setSource(String source) {
                this.source = source;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(firstImg);
                dest.writeString(id);
                dest.writeString(mark);
                dest.writeString(source);
                dest.writeString(title);
                dest.writeString(url);
            }

            @Override
            public String toString() {
                return "ListBean{" +
                        "firstImg='" + firstImg + '\'' +
                        ", id='" + id + '\'' +
                        ", mark='" + mark + '\'' +
                        ", source='" + source + '\'' +
                        ", title='" + title + '\'' +
                        ", url='" + url + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "ResultBean{" +
                    "pno=" + pno +
                    ", ps=" + ps +
                    ", totalPage=" + totalPage +
                    ", list=" + list +
                    '}';
        }
    }


}
