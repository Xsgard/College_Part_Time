const getJobPage=(param)=>{
    return $axios({
        url:'/job',
        method:'get',
        param
    })
}