package server;

import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author: 190coder <190coder.cn>
 * @description: Mapper 路由组件 嵌套xxx子组件组件
 * @create: 2020-06-22 20:03
 */
public class Mapper {


    private Host host;

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    // ------------------------------------------------------- Host Inner Class

    protected static final class Host{

        private String name;
        private String appBase;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAppBase() {
            return appBase;
        }

        public void setAppBase(String appBase) {
            this.appBase = appBase;
        }

        private List<Context> contextList = new ArrayList<>();

        public List<Context> getContextList() {
            return contextList;
        }

        public void setContextList(Context context) {
            this.contextList.add(context);
        }
    }

    // ---------------------------------------------------- Context Inner Class

    protected static final class Context{

        private String contextName;

        private List<Wrapper> wrapperList = new ArrayList<>();

        public String getContextName() {
            return contextName;
        }

        public void setContextName(String contextName) {
            this.contextName = contextName;
        }

        public List<Wrapper> getWrapperList() {
            return wrapperList;
        }

        public void setWrapperList(Wrapper wrapper) {

            this.wrapperList.add(wrapper);
        }
    }

    // ---------------------------------------------------- Wrapper Inner Class

    protected static class Wrapper {

        private Map<String, HttpServlet> wrapperMap = new ConcurrentHashMap<>();

        public HttpServlet getWrapperByUrl(String url) {
            return wrapperMap.get(url);
        }

        public void setWrapperMap(String urlPattern, HttpServlet httpServlet) {
            this.wrapperMap.put(urlPattern,httpServlet);
        }
    }

}
