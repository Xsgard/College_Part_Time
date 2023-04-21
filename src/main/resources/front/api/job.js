//  兼职信息分页接口
const getJobPage = (params) => {
    return $axios({
        url: '/job/page',
        method: 'get',
        params
    })
}
//  已选兼职信息分页接口
const getCheckedJobPage = (params) => {
    return $axios({
        url: '/checkedJob/page',
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
//  删除兼职信息接口
const jobDelete = (id) => {
    return $axios({
        url: '/job',
        method: 'delete',
        params: {id}
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

//  获取用户信息
const getUserList = (id) => {
    return $axios({
        url: `/user/list/${id}`,
        method: 'get'
    })
}

//  新增接口
const addJob = (params) => {
    return $axios({
        url: '/job',
        method: 'post',
        data: {...params}
    })
}

//  查询详情
const queryJobById = (id) => {
    return $axios({
        url: `/job/${id}`,
        method: 'get'
    })
}

//  获取分类列表
const getCategoryList = (params) => {
    return $axios({
        url: '/category/list',
        method: 'get',
        params
    })
}

//  取消已选兼职接口
const checkedJobDel = (ids) => {
    return $axios({
        url: '/checkedJob',
        method: 'delete',
        params: {ids}
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
const getMemberCheckedPage = (params) => {
    return $axios({
        url: '/checkedJob/member',
        method: 'get',
        params
    })
}