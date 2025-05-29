package com.wfh.aiagent.agent;

/**
 * @Author FengHuan Wang
 * @Date 2025/5/29 9:21
 * @Version 1.0
 */
public abstract class ReActAgent extends BaseAgent {

    /**
     * 处理当前状态并决定下一步行动
     * @return
     */
    public abstract boolean think();

    /**
     * 执行决定的行动
     */
    public abstract String act();

    @Override
    public String step() {
        try {
            boolean shouldAct = think();
            if (!shouldAct){
                return "Nothing to do";
            }
            // 执行行动
            return act();
        }catch (Exception e){
            e.printStackTrace();
            return "Act error" + e.getMessage();
        }
    }
}

