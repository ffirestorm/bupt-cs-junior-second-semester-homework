package com.animee.forecast.juhe;
/*
* 聚合数据平台：提供了今日的相关指数信息
* */
public class JHIndexBean {
    /**
     * reason : 查询成功
     * result : {"city":"沈阳","life":{"kongtiao":{"v":"开启制暖空调","des":"您将感到有些冷，可以适当开启制暖空调调节室内温度，以免着凉感冒。"},"guomin":{"v":"极不易发","des":"天气条件极不易诱发过敏，可放心外出，享受生活。"},"shushidu":{"v":"较不舒适","des":"白天天气晴好，但仍会使您感觉偏冷，不很舒适，请注意适时添加衣物，以防感冒。"},"chuanyi":{"v":"寒冷","des":"天气寒冷，建议着厚羽绒服、毛皮大衣加厚毛衣等隆冬服装。年老体弱者尤其要注意保暖防冻。"},"diaoyu":{"v":"不宜","des":"天气冷，不适合垂钓。"},"ganmao":{"v":"较易发","des":"昼夜温差较大，较易发生感冒，请适当增减衣服。体质较弱的朋友请注意防护。"},"ziwaixian":{"v":"弱","des":"紫外线强度较弱，建议出门前涂擦SPF在12-15之间、PA+的防晒护肤品。"},"xiche":{"v":"较适宜","des":"较适宜洗车，未来一天无雨，风力较小，擦洗一新的汽车至少能保持一天。"},"yundong":{"v":"较不宜","des":"天气较好，但考虑天气寒冷，推荐您进行室内运动，户外运动时请注意保暖并做好准备活动。"},"daisan":{"v":"不带伞","des":"天气较好，您在出门的时候无须带雨伞。"}}}
     * error_code : 0
     */
    private String reason;
    private ResultBean result;
    private int error_code;

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

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public static class ResultBean {
        /**
         * city : 沈阳
         * life : {"kongtiao":{"v":"开启制暖空调","des":"您将感到有些冷，可以适当开启制暖空调调节室内温度，以免着凉感冒。"},"guomin":{"v":"极不易发","des":"天气条件极不易诱发过敏，可放心外出，享受生活。"},"shushidu":{"v":"较不舒适","des":"白天天气晴好，但仍会使您感觉偏冷，不很舒适，请注意适时添加衣物，以防感冒。"},"chuanyi":{"v":"寒冷","des":"天气寒冷，建议着厚羽绒服、毛皮大衣加厚毛衣等隆冬服装。年老体弱者尤其要注意保暖防冻。"},"diaoyu":{"v":"不宜","des":"天气冷，不适合垂钓。"},"ganmao":{"v":"较易发","des":"昼夜温差较大，较易发生感冒，请适当增减衣服。体质较弱的朋友请注意防护。"},"ziwaixian":{"v":"弱","des":"紫外线强度较弱，建议出门前涂擦SPF在12-15之间、PA+的防晒护肤品。"},"xiche":{"v":"较适宜","des":"较适宜洗车，未来一天无雨，风力较小，擦洗一新的汽车至少能保持一天。"},"yundong":{"v":"较不宜","des":"天气较好，但考虑天气寒冷，推荐您进行室内运动，户外运动时请注意保暖并做好准备活动。"},"daisan":{"v":"不带伞","des":"天气较好，您在出门的时候无须带雨伞。"}}
         */

        private String city;
        private LifeBean life;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public LifeBean getLife() {
            return life;
        }

        public void setLife(LifeBean life) {
            this.life = life;
        }

        public static class LifeBean {
            /**
             * kongtiao : {"v":"开启制暖空调","des":"您将感到有些冷，可以适当开启制暖空调调节室内温度，以免着凉感冒。"}
             * guomin : {"v":"极不易发","des":"天气条件极不易诱发过敏，可放心外出，享受生活。"}
             * shushidu : {"v":"较不舒适","des":"白天天气晴好，但仍会使您感觉偏冷，不很舒适，请注意适时添加衣物，以防感冒。"}
             * chuanyi : {"v":"寒冷","des":"天气寒冷，建议着厚羽绒服、毛皮大衣加厚毛衣等隆冬服装。年老体弱者尤其要注意保暖防冻。"}
             * diaoyu : {"v":"不宜","des":"天气冷，不适合垂钓。"}
             * ganmao : {"v":"较易发","des":"昼夜温差较大，较易发生感冒，请适当增减衣服。体质较弱的朋友请注意防护。"}
             * ziwaixian : {"v":"弱","des":"紫外线强度较弱，建议出门前涂擦SPF在12-15之间、PA+的防晒护肤品。"}
             * xiche : {"v":"较适宜","des":"较适宜洗车，未来一天无雨，风力较小，擦洗一新的汽车至少能保持一天。"}
             * yundong : {"v":"较不宜","des":"天气较好，但考虑天气寒冷，推荐您进行室内运动，户外运动时请注意保暖并做好准备活动。"}
             * daisan : {"v":"不带伞","des":"天气较好，您在出门的时候无须带雨伞。"}
             */

            private KongtiaoBean kongtiao;
            private GuominBean guomin;
            private ShushiduBean shushidu;
            private ChuanyiBean chuanyi;
            private DiaoyuBean diaoyu;
            private GanmaoBean ganmao;
            private ZiwaixianBean ziwaixian;
            private XicheBean xiche;
            private YundongBean yundong;
            private DaisanBean daisan;

            public KongtiaoBean getKongtiao() {
                return kongtiao;
            }

            public void setKongtiao(KongtiaoBean kongtiao) {
                this.kongtiao = kongtiao;
            }

            public GuominBean getGuomin() {
                return guomin;
            }

            public void setGuomin(GuominBean guomin) {
                this.guomin = guomin;
            }

            public ShushiduBean getShushidu() {
                return shushidu;
            }

            public void setShushidu(ShushiduBean shushidu) {
                this.shushidu = shushidu;
            }

            public ChuanyiBean getChuanyi() {
                return chuanyi;
            }

            public void setChuanyi(ChuanyiBean chuanyi) {
                this.chuanyi = chuanyi;
            }

            public DiaoyuBean getDiaoyu() {
                return diaoyu;
            }

            public void setDiaoyu(DiaoyuBean diaoyu) {
                this.diaoyu = diaoyu;
            }

            public GanmaoBean getGanmao() {
                return ganmao;
            }

            public void setGanmao(GanmaoBean ganmao) {
                this.ganmao = ganmao;
            }

            public ZiwaixianBean getZiwaixian() {
                return ziwaixian;
            }

            public void setZiwaixian(ZiwaixianBean ziwaixian) {
                this.ziwaixian = ziwaixian;
            }

            public XicheBean getXiche() {
                return xiche;
            }

            public void setXiche(XicheBean xiche) {
                this.xiche = xiche;
            }

            public YundongBean getYundong() {
                return yundong;
            }

            public void setYundong(YundongBean yundong) {
                this.yundong = yundong;
            }

            public DaisanBean getDaisan() {
                return daisan;
            }

            public void setDaisan(DaisanBean daisan) {
                this.daisan = daisan;
            }

            public static class KongtiaoBean {
                /**
                 * v : 开启制暖空调
                 * des : 您将感到有些冷，可以适当开启制暖空调调节室内温度，以免着凉感冒。
                 */

                private String v;
                private String des;

                public String getV() {
                    return v;
                }

                public void setV(String v) {
                    this.v = v;
                }

                public String getDes() {
                    return des;
                }

                public void setDes(String des) {
                    this.des = des;
                }
            }

            public static class GuominBean {
                /**
                 * v : 极不易发
                 * des : 天气条件极不易诱发过敏，可放心外出，享受生活。
                 */

                private String v;
                private String des;

                public String getV() {
                    return v;
                }

                public void setV(String v) {
                    this.v = v;
                }

                public String getDes() {
                    return des;
                }

                public void setDes(String des) {
                    this.des = des;
                }
            }

            public static class ShushiduBean {
                /**
                 * v : 较不舒适
                 * des : 白天天气晴好，但仍会使您感觉偏冷，不很舒适，请注意适时添加衣物，以防感冒。
                 */

                private String v;
                private String des;

                public String getV() {
                    return v;
                }

                public void setV(String v) {
                    this.v = v;
                }

                public String getDes() {
                    return des;
                }

                public void setDes(String des) {
                    this.des = des;
                }
            }

            public static class ChuanyiBean {
                /**
                 * v : 寒冷
                 * des : 天气寒冷，建议着厚羽绒服、毛皮大衣加厚毛衣等隆冬服装。年老体弱者尤其要注意保暖防冻。
                 */

                private String v;
                private String des;

                public String getV() {
                    return v;
                }

                public void setV(String v) {
                    this.v = v;
                }

                public String getDes() {
                    return des;
                }

                public void setDes(String des) {
                    this.des = des;
                }
            }

            public static class DiaoyuBean {
                /**
                 * v : 不宜
                 * des : 天气冷，不适合垂钓。
                 */

                private String v;
                private String des;

                public String getV() {
                    return v;
                }

                public void setV(String v) {
                    this.v = v;
                }

                public String getDes() {
                    return des;
                }

                public void setDes(String des) {
                    this.des = des;
                }
            }

            public static class GanmaoBean {
                /**
                 * v : 较易发
                 * des : 昼夜温差较大，较易发生感冒，请适当增减衣服。体质较弱的朋友请注意防护。
                 */

                private String v;
                private String des;

                public String getV() {
                    return v;
                }

                public void setV(String v) {
                    this.v = v;
                }

                public String getDes() {
                    return des;
                }

                public void setDes(String des) {
                    this.des = des;
                }
            }

            public static class ZiwaixianBean {
                /**
                 * v : 弱
                 * des : 紫外线强度较弱，建议出门前涂擦SPF在12-15之间、PA+的防晒护肤品。
                 */

                private String v;
                private String des;

                public String getV() {
                    return v;
                }

                public void setV(String v) {
                    this.v = v;
                }

                public String getDes() {
                    return des;
                }

                public void setDes(String des) {
                    this.des = des;
                }
            }

            public static class XicheBean {
                /**
                 * v : 较适宜
                 * des : 较适宜洗车，未来一天无雨，风力较小，擦洗一新的汽车至少能保持一天。
                 */

                private String v;
                private String des;

                public String getV() {
                    return v;
                }

                public void setV(String v) {
                    this.v = v;
                }

                public String getDes() {
                    return des;
                }

                public void setDes(String des) {
                    this.des = des;
                }
            }

            public static class YundongBean {
                /**
                 * v : 较不宜
                 * des : 天气较好，但考虑天气寒冷，推荐您进行室内运动，户外运动时请注意保暖并做好准备活动。
                 */

                private String v;
                private String des;

                public String getV() {
                    return v;
                }

                public void setV(String v) {
                    this.v = v;
                }

                public String getDes() {
                    return des;
                }

                public void setDes(String des) {
                    this.des = des;
                }
            }

            public static class DaisanBean {
                /**
                 * v : 不带伞
                 * des : 天气较好，您在出门的时候无须带雨伞。
                 */

                private String v;
                private String des;

                public String getV() {
                    return v;
                }

                public void setV(String v) {
                    this.v = v;
                }

                public String getDes() {
                    return des;
                }

                public void setDes(String des) {
                    this.des = des;
                }
            }
        }
    }
}
