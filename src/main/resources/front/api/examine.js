const getExaminePage=(param)=>{
    return $axios({
        url:'/job',
        method:'get',
        param
    })
}