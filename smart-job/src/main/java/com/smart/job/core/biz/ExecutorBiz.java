package com.smart.job.core.biz;


import com.smart.job.core.biz.model.*;

/**
 * Created by xuxueli on 17/3/1.
 */
public interface ExecutorBiz {

    /**
     * beat
     *
     * @return ReturnT
     */
    ReturnT<String> beat();

    /**
     * idle beat
     *
     * @param idleBeatParam 参数
     * @return ReturnT
     */
    ReturnT<String> idleBeat(IdleBeatParam idleBeatParam);

    /**
     * run
     *
     * @param triggerParam 参数
     * @return ReturnT
     */
    ReturnT<String> run(TriggerParam triggerParam);

    /**
     * kill
     *
     * @param killParam 参数
     * @return ReturnT
     */
    ReturnT<String> kill(KillParam killParam);

    /**
     * log
     *
     * @param logParam 参数
     * @return ReturnT
     */
    ReturnT<LogResult> log(LogParam logParam);

}
