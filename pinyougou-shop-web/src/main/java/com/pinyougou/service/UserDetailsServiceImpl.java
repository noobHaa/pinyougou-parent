package com.pinyougou.service;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author ll
 * @Date 2018/11/5 15:50
 */
public class UserDetailsServiceImpl implements UserDetailsService {
    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("经过过滤器");
        TbSeller seller = sellerService.findOne(username);
        List<GrantedAuthority> authorities = new ArrayList();
        authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        //判断用户是否存在
        if (seller != null) {
            //判断用户是否是审核通过的
            if (seller.getStatus().equals("1")) {
                return new User(seller.getSellerId(), seller.getPassword(), authorities);
            } else {
                return null;
            }
        }
        return null;
    }
}
