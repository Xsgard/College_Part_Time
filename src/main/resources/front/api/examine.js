const getExaminePage = (param) => {
    return $axios({
        url: '/user/page',
        method: 'post',
        data: {...param}
    })
}
const passCom = (ids) => {
    return $axios({
        url: `/user/passCom/${ids}`,
        method: 'get'
    })
}
const passJob = (ids) => {
    return $axios({
        url: `/user/passJob/${ids}`,
        method: 'get'
    })
}
//  不通过兼职信息接口
const jobUnPass = (id) => {
    return $axios({
        url: '/job/unPass',
        method: 'delete',
        params: {id}
    })
}
//  不通过企业信息审核接口
const comUnPass = (id) => {
    return $axios({
        url: '/user/unPass',
        method: 'delete',
        params: {id}
    })
}