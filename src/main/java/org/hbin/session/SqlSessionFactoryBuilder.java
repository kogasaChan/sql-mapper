package org.hbin.session;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hbin.constants.SqlCommandType;
import org.hbin.mapping.MappedStatement;
import org.hbin.session.defaults.DefaultSqlSessionFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;

public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(String path) {
        InputStream inputStream = SqlSessionFactoryBuilder.class.getResourceAsStream(path);
        Configuration configuration = parseConfiguration(inputStream);
        return new DefaultSqlSessionFactory(configuration);
    }

    private Configuration parseConfiguration(InputStream inputStream) {
        Configuration configuration = new Configuration();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            Element root = document.getDocumentElement();

            parseDataSource(root, configuration);
            parseMappers(root, configuration);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing configuration.", e);
        }

        return configuration;
    }

    private void parseDataSource(Element root, Configuration configuration) {
        NodeList dataSourceNodes = root.getElementsByTagName("dataSource");

        if (dataSourceNodes.getLength() > 0) {
            Element dataSourceElement = (Element) dataSourceNodes.item(0);
            String driver = getChildValue(dataSourceElement, "driver");
            String url = getChildValue(dataSourceElement, "url");
            String user = getChildValue(dataSourceElement, "username");
            String password = getChildValue(dataSourceElement, "password");

            DataSource dataSource = createDataSource(driver, url, user, password);
            configuration.setDataSource(dataSource);
        }
    }

    private DataSource createDataSource(String driver, String url, String user, String password) {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);

        return dataSource;
    }

    private String getChildValue(Element parent, String childName) {
        if (parent == null || childName == null) {
            throw new IllegalArgumentException("Parent element and child name must not be null or empty.");
        }

        NodeList childNodes = parent.getElementsByTagName(childName);

        if (childNodes.getLength() > 0) {
            return childNodes.item(0).getTextContent().trim();
        }
        return null;
    }

    private void parseMappers(Element root, Configuration configuration) {
        NodeList mapperNodes = root.getElementsByTagName("mapper");

        for (int i = 0; i < mapperNodes.getLength(); i++) {
            Element mapperElement = (Element) mapperNodes.item(i);
            String resource = mapperElement.getAttribute("resource");

            if (resource.isEmpty()) {
                throw new IllegalArgumentException("Mapper 'resource' attribute is missing or empty.");
            }

            try (InputStream mapperStream = SqlSessionFactoryBuilder.class.getResourceAsStream(resource)) {
                if (mapperStream == null) {
                    throw new IllegalArgumentException("Resource not found: " + resource);
                }
                parseMapperXml(mapperStream, configuration);
            } catch (IOException e) {
                throw new RuntimeException("Error while reading mapper resource: " + resource, e);
            }
        }
    }

    private void parseMapperXml(InputStream inputStream, Configuration configuration) {
        try {
            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(inputStream);

            Element root = document.getDocumentElement();
            String namespace = root.getAttribute("namespace");

            if (namespace.isEmpty()) {
                throw new IllegalArgumentException("Namespace not specified in the XML.");
            }

            Class<?> mapperClass = Class.forName(namespace);
            if (!configuration.hasMapper(mapperClass)) {
                configuration.addMapper(mapperClass);
            }

            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    parseSqlNode((Element) node, namespace, configuration);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse mapper XML.", e);
        }
    }

    private void parseSqlNode(Element element, String namespace, Configuration configuration) throws ClassNotFoundException {
        String tagName = element.getTagName();

        SqlCommandType commandType = SqlCommandType.fromTagName(tagName);
        if (commandType == null) {
            return;
        }

        String id = element.getAttribute("id");
        String sql = element.getTextContent().trim();
        String resultType = element.getAttribute("resultType");
        Class<?> cl = null;
        if (!resultType.isEmpty()) {
            cl = Class.forName(resultType);
        }

        MappedStatement ms = new MappedStatement(namespace, id, sql, commandType, cl);
        configuration.addMappedStatement(ms.getStatementId(), ms);
    }

}
