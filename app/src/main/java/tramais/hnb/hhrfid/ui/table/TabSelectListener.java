package tramais.hnb.hhrfid.ui.table;

public interface TabSelectListener {
    /**
     * tab选择
     *
     * @param position
     * @return 返回false时表示拦截该事件
     */
    boolean onTabSelect(int position);

    /**
     * 重复点击
     *
     * @param position
     * @return 返回false时表示拦截该事件
     */
    boolean onTabReselect(int position);
}
