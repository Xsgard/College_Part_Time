//获取Post分页
const getPostPage = (params) => {
    return $axios({
        url: '/forum/postPage',
        method: 'get',
        params
    })
}
const getPost = (id) => {
    return $axios({
        url: `/forum/${id}`,
        method: 'get'
    })
}

//  新增接口
const addPost = (params) => {
    return $axios({
        url: '/forum',
        method: 'post',
        data: {...params}
    })
}

const submitRateValue = (postRate) => {
    return $axios({
        url: '/forum/rateValue',
        method: 'put',
        data: {...postRate}
    })
}

const addComment = (params) => {
    return $axios({
        url: '/forum/addComment',
        method: 'post',
        data: {...params}
    })
}


