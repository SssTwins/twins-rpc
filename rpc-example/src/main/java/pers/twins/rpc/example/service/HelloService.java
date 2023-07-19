package pers.twins.rpc.example.service;

/**
 * @author twins
 * @date 2023-07-19 21:49:36
 */
public interface HelloService {

    /**
     * hello test
     *
     * @param param param
     * @return Hello: + ${param}
     */
    String hello(String param);
}
