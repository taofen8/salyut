package com.trico.salyut.engine;

public class ExecResult {
    public final Object val;
    public final InterruptType intType;
    public String jobId; /** optional */

//    private static final ExecResult BREAK = new ExecResult.Builder().type(InterruptType.BREAK).build();
//    private static final ExecResult CONTINUE = new ExecResult.Builder().type(InterruptType.CONTINUE).build();
//    private static final ExecResult THROUGH = new ExecResult.Builder().type(InterruptType.THROUGH).build();

    public enum InterruptType{
        /** 正常返回 */
        RETURN,
        BREAK,
        CONTINUE,
        /** 正常执行 */
        THROUGH,
        /** 执行失败 */
        FAILED
    }

    private ExecResult(Builder builder){
        this.val = builder.val;
        this.intType = builder.intType;
        this.jobId = builder.jobId;
    }

    public ExecResult copy(){
        return new Builder()
                .val(val)
                .type(intType)
                .jobId(jobId)
                .build();
    }

    public static class Builder{
        Object val;
        InterruptType intType;
        String jobId;

        public Builder val(Object val){
            this.val = val;
            return this;
        }

        public Builder type(InterruptType intType){
            this.intType = intType;
            return this;
        }

        public Builder jobId(String jobId){
            this.jobId = jobId;
            return this;
        }

        public ExecResult build(){
            return new ExecResult(this);
        }
    }

    public static ExecResult getReturn(Object val){
        return new ExecResult.Builder().val(val).type(InterruptType.RETURN).build();
    }

    public static ExecResult getBreak(){
        return new ExecResult.Builder().type(InterruptType.BREAK).build();

    }

    public static ExecResult getContinue(){
        return new ExecResult.Builder().type(InterruptType.CONTINUE).build();
    }

    public static ExecResult getThrough(){
        return new ExecResult.Builder().type(InterruptType.THROUGH).build();
    }
}
