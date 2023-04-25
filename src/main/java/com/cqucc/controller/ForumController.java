package com.cqucc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqucc.common.R;
import com.cqucc.dto.PostCommentDto;
import com.cqucc.dto.PostDto;
import com.cqucc.entity.Category;
import com.cqucc.entity.Post;
import com.cqucc.entity.User;
import com.cqucc.service.CategoryService;
import com.cqucc.service.CommentService;
import com.cqucc.service.PostService;
import com.cqucc.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.soap.Addressing;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/forum")
public class ForumController {

    @Autowired
    public PostService postService;

    @Autowired
    public CommentService commentService;

    @Autowired
    public UserService userService;

    @Autowired
    public CategoryService categoryService;

    /**
     * 获取Post分页
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/postPage")
    public R<Page<PostDto>> getPostPage(Integer page, Integer pageSize, String name) {
        Page<Post> postPage = new Page<>(page, pageSize);
        Page<PostDto> dtoPage = new Page<>();
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Post::getTitle, name);
        postService.page(postPage);

        BeanUtils.copyProperties(postPage, dtoPage, "records");
        List<Post> records = postPage.getRecords();
        List<PostDto> dtoList = records.stream().map((item) -> {
            PostDto postDto = new PostDto();
            BeanUtils.copyProperties(item, postDto);
            User user = userService.getById(item.getCreateUser());
            Category category = categoryService.getById(postDto.getCategoryId());
            postDto.setCreator(user.getName());
            postDto.setCategoryName(category.getName());
            postDto.setContent(null);
            return postDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(dtoList);
        return R.success(dtoPage);
    }

    @PostMapping("/getPost")
    public R<PostCommentDto> getPost(@RequestBody Long postId) {
        return null;
    }

    @DeleteMapping
    public R<String> deletePost(@RequestBody Long postId) {

        return null;
    }

    private Double RateValueCalculator(Integer pageView,Double oldRateValue) {

        return null;
    }

}
