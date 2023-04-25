//获取Post分页
const getPostPage = (params) => {
    return $axios({
        url: '/forum/postPage',
        method: 'get',
        params
    })
}

