package pers.twins.rpc.example.service;

/**
 * @author twins
 * @date 2023-07-19 21:49:56
 */
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String param) {
        return "Hello: " + param;
    }
}
