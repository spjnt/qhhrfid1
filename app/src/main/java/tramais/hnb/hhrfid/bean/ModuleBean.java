package tramais.hnb.hhrfid.bean;

public class ModuleBean {
    private String module_name;
    private int module_bit;

    public ModuleBean(String module_name, int module_bit) {
        this.module_name = module_name;
        this.module_bit = module_bit;
    }

    public String getModule_name() {
        return module_name;
    }

    public void setModule_name(String module_name) {
        this.module_name = module_name;
    }

    public int getModule_bit() {
        return module_bit;
    }

    public void setModule_bit(int module_bit) {
        this.module_bit = module_bit;
    }
}
