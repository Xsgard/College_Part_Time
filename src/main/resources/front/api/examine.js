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