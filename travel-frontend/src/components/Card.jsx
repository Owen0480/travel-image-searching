function Card({ imageUrl, title, subtitle, matchRate }) {
    return (
        <div className="group relative bg-white dark:bg-slate-800 rounded-xl overflow-hidden shadow-sm hover:shadow-xl transition-all border border-[#e7eef3] dark:border-slate-700">
            <div className="absolute top-3 right-3 z-10">
                <input
                    type="checkbox"
                    className="w-6 h-6 rounded text-primary focus:ring-primary border-white/40 bg-black/20 backdrop-blur-sm cursor-pointer"
                />
            </div>

            <div
                className="bg-cover bg-center h-64 flex flex-col justify-end p-4 relative"
                style={{
                    backgroundImage: `
                      linear-gradient(0deg, rgba(0,0,0,0.7) 0%, rgba(0,0,0,0) 50%),
                      url(${imageUrl})
                    `
                }}
            >
                <div className="absolute top-3 left-3 px-2 py-1 rounded bg-primary text-white text-[10px] font-bold tracking-widest uppercase">
                    {matchRate}% Match
                </div>

                <h3 className="text-white text-lg font-bold leading-tight">
                    {title}
                </h3>
                <p className="text-white/80 text-xs font-medium">
                    {subtitle}
                </p>
            </div>

            <div className="p-4 flex justify-between items-center">
                <button className="text-primary text-sm font-bold hover:underline">
                    View details
                </button>
                <span className="material-symbols-outlined text-slate-400 group-hover:text-primary transition-colors">
                    favorite
                </span>
            </div>
        </div>
    )
}

export default Card
