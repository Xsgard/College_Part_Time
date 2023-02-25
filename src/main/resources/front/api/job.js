// 查询列表接口
const getJobPage = (params) => {
    return $axios({
        url: '/job/page',
        method: 'get',
        params
    })
}


// 应聘接口
const checkJob = (ids) => {
    return $axios({
        url: `/job/checkJob/${ids}`,
        method: 'get'
    })
}

// 修改接口
const editJob = (params) => {
    return $axios({
        url: '/job',
        method: 'put',
        data: {...params}
    })
}

//获取用户信息
const getUserList = (id) => {
    return $axios({
        url: `/user/list/${id}`,
        method: 'get'
    })
}

// 新增接口
const addJob = (params) => {
    return $axios({
        url: '/job',
        method: 'post',
        data: {...params}
    })
}

// 查询详情
const queryJobById = (id) => {
    return $axios({
        url: `/job/${id}`,
        method: 'get'
    })
}

// 获取菜品分类列表
const getCategoryList = (params) => {
    return $axios({
        url: '/category/list',
        method: 'get',
        params
    })
}

// 查菜品列表的接口
const userUpdate = (params) => {
    return $axios({
        url: '/user',
        method: 'put',
        params
    })
}

// 文件down预览
const commonDownload = (params) => {
    return $axios({
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
        },
        url: '/common/download',
        method: 'get',
        params
    })
}

// 起售停售---批量起售停售接口
const dishStatusByStatus = (params) => {
    return $axios({
        url: `/dish/status/${params.status}`,
        method: 'post',
        params: {ids: params.id}
    })
}