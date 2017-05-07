package hr.fer.zemris.util;

import java.util.List;

/**
 * Created by generalic on 4.5.2016..
 */
public class JSONGraph {

    /**
     * name : Myriel
     * group : 1
     */
    private List<NodesBean> nodes;

    /**
     * source : 1
     * target : 0
     * value : 1
     */
    private List<LinksBean> links;

    public List<NodesBean> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodesBean> nodes) {
        this.nodes = nodes;
    }

    public List<LinksBean> getLinks() {
        return links;
    }

    public void setLinks(List<LinksBean> links) {
        this.links = links;
    }

    public static class NodesBean {
        private String name;
        private int group;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getGroup() {
            return group;
        }

        public void setGroup(int group) {
            this.group = group;
        }
    }

    public static class LinksBean {
        private int source;
        private int target;
        private int value;

        public int getSource() {
            return source;
        }

        public void setSource(int source) {
            this.source = source;
        }

        public int getTarget() {
            return target;
        }

        public void setTarget(int target) {
            this.target = target;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
