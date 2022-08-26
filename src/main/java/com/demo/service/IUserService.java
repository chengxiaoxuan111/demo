package com.demo.service;

/**
 * @author 程啸轩
 * @date 2022/8/26 16:22
 */
public interface IUserService extends IService<User> {
    /**
     * 获取用户信息
     *
     * @param username 用户名
     * @return {@code User}
     */
    User getUserByUsername(String username);

    /**
     * 得到当前用户的权限列表
     *
     * @param userId 用户id
     * @return {@code List<Resource>}
     */
    List<Resource> getUserPermission(Long userId);

    /**
     * 用户注册
     *
     * @param userDTO 用户dto
     * @return boolean
     */
    boolean registry(UserDTO userDTO);

    /**
     * 登录,测试demo，就没加验证码
     *
     * @param username 用户名
     * @param password 密码
     * @return {@code String}
     */
    String login(String username, String password);

