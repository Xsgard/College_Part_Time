package com.cqucc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqucc.common.R;
import com.cqucc.dto.PostCommentDto;
import com.cqucc.dto.PostDto;
import com.cqucc.entity.Category;
import com.cqucc.entity.Comment;
import com.cqucc.entity.Post;
import com.cqucc.entity.User;
import com.cqucc.service.CategoryService;
import com.cqucc.service.CommentService;
import com.cqucc.service.PostService;
import com.cqucc.service.UserService;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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
    public R<Page<PostDto>> getPostPage(Integer page, Integer pageSize, String name, Long categoryId) {
        Page<Post> postPage = new Page<>(page, pageSize);
        Page<PostDto> dtoPage = new Page<>();
        boolean flag = categoryId == null;
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(!flag, Post::getCategoryId, categoryId);
        queryWrapper.like(StringUtils.isNotEmpty(name), Post::getTitle, name);
        postService.page(postPage, queryWrapper);

        BeanUtils.copyProperties(postPage, dtoPage, "records");
        List<Post> records = postPage.getRecords();
        List<PostDto> dtoList = records.stream().map((item) -> {
            PostDto postDto = new PostDto();
            BeanUtils.copyProperties(item, postDto);
            User user = userService.getById(item.getCreateUser());
            Category category = categoryService.getById(postDto.getCategoryId());
            postDto.setPoster(user.getName());
            postDto.setCategoryName(category.getName());
            postDto.setContent(null);
            return postDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(dtoList);
        return R.success(dtoPage);
    }

    @GetMapping("/{id}")
    public R<PostCommentDto> getPost(@PathVariable Long id) {
        PostCommentDto dto = new PostCommentDto();
        Post post = postService.getById(id);
        Integer pageView = post.getPageView();
        post.setPageView(pageView + 1);
        postService.updateById(post);
        BeanUtils.copyProperties(post, dto);
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getPostId, id);
        List<Comment> commentList = commentService.list(queryWrapper);
        dto.setComments(commentList);
        return R.success(dto);
    }

    @PutMapping("/rateValue")
    public R<String> rateValue(@RequestBody PostRate postRate) {
        Post post = postService.getById(postRate.getPostId());
        double score = calculateScore(postRate.getRateValue(), post.getRateValue(), post.getPageView());
        post.setRateValue(score);
        postService.updateById(post);
        return R.success("评分成功！");
    }

    @PostMapping("/addComment")
    public R<String> addComment(@RequestBody Comment comment) {
        commentService.save(comment);
        return R.success("添加成功！");
    }

    @PostMapping("/delComment")
    public R<String> delComment(@RequestBody Comment comment) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getPostId, comment.getPostId());
        queryWrapper.eq(Comment::getId, comment.getId());
        commentService.remove(queryWrapper);
        return R.success("删除成功！");
    }

    @PostMapping
    public R<String> addPost(@RequestBody Post post) {
        postService.save(post);
        return R.success("添加成功！");
    }

    @DeleteMapping
    @Transactional
    public R<String> deletePost(Long id) {
        postService.removeById(id);
        return R.success("删除成功！");
    }

    /**
     * 根据用户给的rateValue（0-5）、原本的oldRateValue和pageViews计算得分，上限5.0
     *
     * @param rateValue    用户给定的rateValue，取值范围为[0,5]之间的任意浮点数。
     * @param oldRateValue 原本的oldRateValue，取值范围为[0,5]之间的任意浮点数。
     * @param pageViews    浏览量，表示网页被访问的次数。
     * @return 计算得分，上限为5.0.
     */
    public static double calculateScore(double rateValue, double oldRateValue, int pageViews) {
        // 可以根据具体场景修改每个参数对于总得分的权重
        double rateWeight = 0.5; // rateWeight 的权重
        double oldRateWeight = 0.3; // oldRateWeight 的权重
        double viewsWeight = 0.2; // viewsWeight 的权重

        // 固定的一些常量
        double maxScore = 5.0; // 总得分上限
        double decayFactor = 0.8; // 旧评分衰减因子
        double minViews = 1.0; // 最小浏览量，用于避免除零错误

        if (rateValue == 0.0) {
            rateValue = oldRateValue;
        }

        // 计算每个部分的得分
        double rateScore = Math.min(rateValue, maxScore) / maxScore * rateWeight;
        double oldRateScore = (Math.pow(decayFactor, oldRateValue) * oldRateValue) / maxScore * oldRateWeight;
        double viewsScore = Math.log(Math.max(pageViews, minViews)) / Math.log(minViews + pageViews) * viewsWeight;

        // 计算总得分
        double score = rateScore + oldRateScore + viewsScore;
        score = Math.min(score, maxScore);

        return score;
    }

    @Data
    public static class PostRate {
        private Integer rateValue;
        private Long postId;
    }

}
